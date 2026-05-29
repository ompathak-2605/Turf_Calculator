package com.example.demo.service;

import com.example.demo.dto.request.CreateMatchRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository  teamRepository;

    /**
     * Creates a match, its custom rules, and both teams in one transaction.
     * Every rule field is optional — defaults are applied here.
     */
    public Match createMatch(CreateMatchRequest req) {

        MatchRules rules = MatchRules.builder()
                .totalOvers        (orDefault(req.getTotalOvers(),       10))
                .playersPerTeam    (orDefault(req.getPlayersPerTeam(),    6))
                .wideBallRuns      (orDefault(req.getWideBallRuns(),      1))
                .noBallRuns        (orDefault(req.getNoBallRuns(),        1))
                .freeHitOnNoBall   (orDefault(req.getFreeHitOnNoBall(),   false))
                .maxOversPerBowler (req.getMaxOversPerBowler())           // null = no limit
                .wicketsToFinish   (req.getWicketsToFinish())             // null = all out
                .boundaryRules     (orDefault(req.getBoundaryRules(),     true))
                .fourRuns          (orDefault(req.getFourRuns(),          4))
                .sixRuns           (orDefault(req.getSixRuns(),           6))
                .customNotes       (req.getCustomNotes())
                .build();

        Match match = Match.builder()
                .name   (req.getName())
                .venue  (req.getVenue())
                .date   (req.getDate())
                .status (Match.MatchStatus.UPCOMING)
                .rules  (rules)
                .build();

        match = matchRepository.save(match);

        Team t1 = Team.builder().name(req.getTeam1Name()).match(match).build();
        Team t2 = Team.builder().name(req.getTeam2Name()).match(match).build();
        teamRepository.saveAll(Arrays.asList(t1, t2));

        return match;
    }

    public Match startMatch(Long matchId) {
        Match match = getMatchById(matchId);
        match.setStatus(Match.MatchStatus.IN_PROGRESS);
        return matchRepository.save(match);
    }

    public Match completeMatch(Long matchId) {
        Match match = getMatchById(matchId);
        match.setStatus(Match.MatchStatus.COMPLETED);
        return matchRepository.save(match);
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found: " + id));
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchesByStatus(Match.MatchStatus status) {
        return matchRepository.findByStatus(status);
    }

    // ── helpers ──────────────────────────────────────────────
    private <T> T orDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}