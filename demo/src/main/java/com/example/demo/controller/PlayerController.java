package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    /**
     * POST /api/players/team/{teamId}
     * Add a single player.
     * Body: { "name": "Rohit Sharma" }
     */
    @PostMapping("/team/{teamId}")
    public ResponseEntity<Player> addPlayer(
            @PathVariable Long teamId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(playerService.addPlayer(teamId, body.get("name")));
    }

    /**
     * POST /api/players/team/{teamId}/bulk
     * Add all squad members at once.
     * Body: ["Rohit", "Virat", "Dhoni", "Bumrah", "Shami", "Jadeja"]
     */
    @PostMapping("/team/{teamId}/bulk")
    public ResponseEntity<List<Player>> addBulkPlayers(
            @PathVariable Long teamId,
            @RequestBody List<String> names) {
        return ResponseEntity.ok(playerService.addBulkPlayers(teamId, names));
    }

    /** GET /api/players/team/{teamId} */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Player>> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    /** GET /api/players/match/{matchId}  — all players from both teams */
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<Player>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(playerService.getPlayersByMatch(matchId));
    }

    /** GET /api/players/match/{matchId}/teams  — just the two Team objects */
    @GetMapping("/match/{matchId}/teams")
    public ResponseEntity<List<Team>> getTeams(@PathVariable Long matchId) {
        return ResponseEntity.ok(playerService.getTeamsByMatch(matchId));
    }
}