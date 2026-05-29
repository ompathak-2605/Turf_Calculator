package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository   teamRepository;

    public Player addPlayer(Long teamId, String name) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        return playerRepository.save(
                Player.builder().name(name).team(team).build());
    }

    /** Add all squad members in one call — useful for setup before match start */
    public List<Player> addBulkPlayers(Long teamId, List<String> names) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        List<Player> players = names.stream()
                .map(n -> Player.builder().name(n).team(team).build())
                .toList();

        return playerRepository.saveAll(players);
    }

    public List<Player> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public List<Player> getPlayersByMatch(Long matchId) {
        return playerRepository.findByTeamMatchId(matchId);
    }

    public List<Team> getTeamsByMatch(Long matchId) {
        return teamRepository.findByMatchId(matchId);
    }

    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
    }
}