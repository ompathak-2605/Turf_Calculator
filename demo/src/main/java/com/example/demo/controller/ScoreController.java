package com.example.demo.controller;

import com.example.demo.dto.request.AddBallEventRequest;
import com.example.demo.entity.*;
import com.example.demo.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/score")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * POST /api/score/innings/start?matchId=1&battingTeamId=1&bowlingTeamId=2&inningsNumber=1
     * Call this to begin each team's batting innings.
     */
    @PostMapping("/innings/start")
    public ResponseEntity<Innings> startInnings(
            @RequestParam Long    matchId,
            @RequestParam Long    battingTeamId,
            @RequestParam Long    bowlingTeamId,
            @RequestParam Integer inningsNumber) {
        return ResponseEntity.ok(
                scoreService.startInnings(matchId, battingTeamId, bowlingTeamId, inningsNumber));
    }

    /**
     * POST /api/score/ball
     * Send one delivery at a time.
     *
     * Minimum body for a dot ball:
     * { "inningsId": 1, "batterId": 3, "bowlerId": 8, "runs": 0 }
     *
     * Wide:
     * { "inningsId": 1, "batterId": 3, "bowlerId": 8, "runs": 0, "isWide": true }
     *
     * Wicket (caught):
     * { "inningsId": 1, "batterId": 3, "bowlerId": 8, "runs": 0,
     *   "isWicket": true, "wicketType": "CAUGHT", "fielderId": 12 }
     */
    @PostMapping("/ball")
    public ResponseEntity<BallEvent> addBall(@RequestBody AddBallEventRequest request) {
        return ResponseEntity.ok(scoreService.addBallEvent(request));
    }

    /** GET /api/score/innings/{matchId}  — all innings for a match */
    @GetMapping("/innings/{matchId}")
    public ResponseEntity<List<Innings>> getInnings(@PathVariable Long matchId) {
        return ResponseEntity.ok(scoreService.getInningsByMatch(matchId));
    }

    /** GET /api/score/batting-card/{inningsId}  — individual batter scores */
    @GetMapping("/batting-card/{inningsId}")
    public ResponseEntity<List<BattingPerformance>> getBattingCard(@PathVariable Long inningsId) {
        return ResponseEntity.ok(scoreService.getBattingCard(inningsId));
    }

    /** GET /api/score/bowling-card/{inningsId}  — bowler figures */
    @GetMapping("/bowling-card/{inningsId}")
    public ResponseEntity<List<BowlingPerformance>> getBowlingCard(@PathVariable Long inningsId) {
        return ResponseEntity.ok(scoreService.getBowlingCard(inningsId));
    }

    /**
     * GET /api/score/ball-by-ball/{inningsId}
     * Returns every delivery in order — useful for an over-by-over view.
     */
    @GetMapping("/ball-by-ball/{inningsId}")
    public ResponseEntity<List<BallEvent>> getBallByBall(@PathVariable Long inningsId) {
        return ResponseEntity.ok(scoreService.getBallByBall(inningsId));
    }
}