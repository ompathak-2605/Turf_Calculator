// ── BattingPerformanceRepository.java ────────────────────────
package com.example.demo.repository;

import com.example.demo.entity.BattingPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BattingPerformanceRepository extends JpaRepository<BattingPerformance, Long> {
    List<BattingPerformance>     findByInningsId(Long inningsId);
    Optional<BattingPerformance> findByPlayerIdAndInningsId(Long playerId, Long inningsId);
}
 