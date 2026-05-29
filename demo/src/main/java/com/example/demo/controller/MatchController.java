package com.example.demo.controller;

import com.example.demo.dto.request.CreateMatchRequest;
import com.example.demo.entity.Match;
import com.example.demo.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * POST /api/matches
     * Creates a match, its rules, and both teams.
     *
     * Example body:
     * {
     *   "name": "Friday T8",
     *   "venue": "Green Turf, Salt Lake",
     *   "date": "2026-05-26",
     *   "team1Name": "Chota_Bheem",
     *   "team2Name": "Kaaliya",
     *   "totalOvers": 8,
     *   "playersPerTeam": 6,
     *   "wideBallRuns": 2,
     *   "wicketsToFinish": 2,
     *   "customNotes": "Free hit on every no-ball."
     * }
     */
    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody CreateMatchRequest request) {
        return ResponseEntity.ok(matchService.createMatch(request));
    }

    /** GET /api/matches */
    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    /** GET /api/matches/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatch(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    /** PUT /api/matches/{id}/start  — changes status to IN_PROGRESS */
    @PutMapping("/{id}/start")
    public ResponseEntity<Match> startMatch(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.startMatch(id));
    }

    /** PUT /api/matches/{id}/complete */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Match> completeMatch(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.completeMatch(id));
    }

    /** GET /api/matches/status/{status}  e.g. UPCOMING / IN_PROGRESS / COMPLETED */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Match>> getByStatus(@PathVariable Match.MatchStatus status) {
        return ResponseEntity.ok(matchService.getMatchesByStatus(status));
    }
}