// ── InningsRepository.java ────────────────────────────────────
package com.example.demo.repository;

import com.example.demo.entity.Innings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InningsRepository extends JpaRepository<Innings, Long> {
    List<Innings>     findByMatchId(Long matchId);
    Optional<Innings> findByMatchIdAndInningsNumber(Long matchId, Integer inningsNumber);
    List<Innings>     findByBattingTeamId(Long teamId);
}
 