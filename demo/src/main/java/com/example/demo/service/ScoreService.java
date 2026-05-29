package com.example.demo.service;

import com.example.demo.dto.request.AddBallEventRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Core scoring engine.
 *
 * Flow for each ball:
 *   1. Determine over/ball number
 *   2. Calculate bat runs + extra runs using the match's MatchRules
 *   3. Update Innings totals
 *   4. Update BattingPerformance for the on-strike batter
 *   5. Update BowlingPerformance for the bowler
 *   6. Persist the BallEvent
 *   7. Check if innings is complete (overs done OR wickets limit reached)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScoreService {

    private final MatchRepository               matchRepository;
    private final InningsRepository             inningsRepository;
    private final BallEventRepository           ballEventRepository;
    private final BattingPerformanceRepository  battingRepo;
    private final BowlingPerformanceRepository  bowlingRepo;

    // ─── Innings management ───────────────────────────────────

    public Innings startInnings(Long matchId, Long battingTeamId,
                                Long bowlingTeamId, Integer inningsNumber) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        Innings innings = Innings.builder()
                .match(match)
                .battingTeam(Team.builder().id(battingTeamId).build())
                .bowlingTeam(Team.builder().id(bowlingTeamId).build())
                .inningsNumber(inningsNumber)
                .build();

        return inningsRepository.save(innings);
    }

    // ─── Ball entry ───────────────────────────────────────────

    public BallEvent addBallEvent(AddBallEventRequest req) {
        Innings innings = inningsRepository.findById(req.getInningsId())
                .orElseThrow(() -> new RuntimeException("Innings not found: " + req.getInningsId()));

        MatchRules rules = innings.getMatch().getRules();

        // ── Determine current position in the innings ─────────
        int legalSoFar  = safe(innings.getBallsBowled());
        int overNumber   = legalSoFar / 6;
        int ballInOver   = legalSoFar % 6;

        boolean isWide   = bool(req.getIsWide());
        boolean isNoBall = bool(req.getIsNoBall());
        boolean isBye    = bool(req.getIsBye());
        boolean isLegBye = bool(req.getIsLegBye());
        boolean isWicket = bool(req.getIsWicket());
        int     batRuns  = safe(req.getRuns());

        // ── Calculate extra runs using the match rules ────────
        int extraRuns = 0;

        if (isWide) {
            extraRuns += safe(rules.getWideBallRuns(), 1);  // penalty run(s) for wide
            extraRuns += batRuns;                            // runs run off a wide still count
            innings.setWides(safe(innings.getWides()) + 1);
            batRuns = 0;                                     // don't credit batter for wide runs
        } else if (isNoBall) {
            extraRuns += safe(rules.getNoBallRuns(), 1);
            innings.setNoBalls(safe(innings.getNoBalls()) + 1);
        }

        // Byes and leg-byes: runs go to extras, not batter
        if (isBye) {
            extraRuns += batRuns;
            innings.setByes(safe(innings.getByes()) + batRuns);
            batRuns = 0;
        } else if (isLegBye) {
            extraRuns += batRuns;
            innings.setLegByes(safe(innings.getLegByes()) + batRuns);
            batRuns = 0;
        }

        // ── Build the BallEvent ───────────────────────────────
        BallEvent event = BallEvent.builder()
                .innings    (innings)
                .batter     (playerRef(req.getBatterId()))
                .nonStriker (req.getNonStrikerId() != null ? playerRef(req.getNonStrikerId()) : null)
                .bowler     (playerRef(req.getBowlerId()))
                .fielder    (req.getFielderId()    != null ? playerRef(req.getFielderId())    : null)
                .overNumber (overNumber)
                .ballNumber (ballInOver + 1)
                .runs       (batRuns)
                .totalRuns  (batRuns + extraRuns)
                .isWicket   (isWicket)
                .isWide     (isWide)
                .isNoBall   (isNoBall)
                .isBye      (isBye)
                .isLegBye   (isLegBye)
                .wicketType (req.getWicketType() != null ? req.getWicketType() : BallEvent.WicketType.NONE)
                .build();

        // ── Update innings running totals ─────────────────────
        innings.setTotalRuns(safe(innings.getTotalRuns()) + batRuns + extraRuns);
        innings.setExtras   (safe(innings.getExtras())    + extraRuns);

        boolean isLegal = !isWide && !isNoBall;
        if (isLegal) {
            innings.setBallsBowled(safe(innings.getBallsBowled()) + 1);
        }

        if (isWicket) {
            innings.setTotalWickets(safe(innings.getTotalWickets()) + 1);
            dismissBatter(req, innings);
        } else if (!isBye && !isLegBye) {
            creditBatterRuns(req.getBatterId(), innings, batRuns, isLegal);
        }

        updateBowler(req, innings, batRuns + extraRuns, isLegal);
        checkInningsComplete(innings, rules);

        inningsRepository.save(innings);
        return ballEventRepository.save(event);
    }

    // ─── Scorecard queries ────────────────────────────────────

    public Innings getInnings(Long inningsId) {
        return inningsRepository.findById(inningsId)
                .orElseThrow(() -> new RuntimeException("Innings not found"));
    }

    public List<Innings> getInningsByMatch(Long matchId) {
        return inningsRepository.findByMatchId(matchId);
    }

    public List<BattingPerformance> getBattingCard(Long inningsId) {
        return battingRepo.findByInningsId(inningsId);
    }

    public List<BowlingPerformance> getBowlingCard(Long inningsId) {
        return bowlingRepo.findByInningsId(inningsId);
    }

    public List<BallEvent> getBallByBall(Long inningsId) {
        return ballEventRepository.findByInningsIdOrderByOverNumberAscBallNumberAsc(inningsId);
    }

    // ─── Private helpers ──────────────────────────────────────

    private void creditBatterRuns(Long batterId, Innings innings, int runs, boolean countBall) {
        BattingPerformance bp = battingRepo
                .findByPlayerIdAndInningsId(batterId, innings.getId())
                .orElseGet(() -> newBatting(batterId, innings));

        bp.setRunsScored(safe(bp.getRunsScored()) + runs);
        if (countBall) bp.setBallsFaced(safe(bp.getBallsFaced()) + 1);
        if (runs == 4) bp.setFours(safe(bp.getFours()) + 1);
        if (runs == 6) bp.setSixes(safe(bp.getSixes()) + 1);
        battingRepo.save(bp);
    }

    private void dismissBatter(AddBallEventRequest req, Innings innings) {
        BattingPerformance bp = battingRepo
                .findByPlayerIdAndInningsId(req.getBatterId(), innings.getId())
                .orElseGet(() -> newBatting(req.getBatterId(), innings));

        bp.setIsOut(true);
        bp.setDismissalType(req.getWicketType());
        bp.setDidBat(true);
        if (req.getBowlerId() != null) bp.setBowler(playerRef(req.getBowlerId()));
        if (req.getFielderId() != null) bp.setFielder(playerRef(req.getFielderId()));
        battingRepo.save(bp);
    }

    private void updateBowler(AddBallEventRequest req, Innings innings,
                              int totalRunsThisBall, boolean isLegal) {
        BowlingPerformance bp = bowlingRepo
                .findByPlayerIdAndInningsId(req.getBowlerId(), innings.getId())
                .orElseGet(() -> newBowling(req.getBowlerId(), innings));

        bp.setBallsBowled(safe(bp.getBallsBowled()) + 1);
        if (isLegal) bp.setLegalDeliveries(safe(bp.getLegalDeliveries()) + 1);
        bp.setRunsConceded(safe(bp.getRunsConceded()) + totalRunsThisBall);
        if (bool(req.getIsWide()))   bp.setWides(safe(bp.getWides()) + 1);
        if (bool(req.getIsNoBall())) bp.setNoBalls(safe(bp.getNoBalls()) + 1);

        // Run-outs don't count as bowler's wicket
        if (bool(req.getIsWicket()) &&
                req.getWicketType() != BallEvent.WicketType.RUN_OUT) {
            bp.setWicketsTaken(safe(bp.getWicketsTaken()) + 1);
        }
        bowlingRepo.save(bp);
    }

    private void checkInningsComplete(Innings innings, MatchRules rules) {
        int maxLegalBalls = safe(rules.getTotalOvers(), 10) * 6;
        int maxWickets    = rules.getWicketsToFinish() != null
                ? rules.getWicketsToFinish()
                : safe(rules.getPlayersPerTeam(), 6) - 1;

        if (safe(innings.getBallsBowled()) >= maxLegalBalls ||
                safe(innings.getTotalWickets()) >= maxWickets) {
            innings.setIsCompleted(true);
        }
    }

    private BattingPerformance newBatting(Long playerId, Innings innings) {
        return BattingPerformance.builder()
                .player(playerRef(playerId)).innings(innings).build();
    }

    private BowlingPerformance newBowling(Long playerId, Innings innings) {
        return BowlingPerformance.builder()
                .player(playerRef(playerId)).innings(innings).build();
    }

    private Player playerRef(Long id) {
        return Player.builder().id(id).build();
    }

    private int safe(Integer v)            { return v != null ? v : 0; }
    private int safe(Integer v, int def)   { return v != null ? v : def; }
    private boolean bool(Boolean v)        { return Boolean.TRUE.equals(v); }
}