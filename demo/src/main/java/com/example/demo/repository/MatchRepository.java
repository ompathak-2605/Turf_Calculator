// ── MatchRepository.java ──────────────────────────────────────
package com.example.demo.repository;

import com.example.demo.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(Match.MatchStatus status);
    List<Match> findByVenueContainingIgnoreCase(String venue);
}
