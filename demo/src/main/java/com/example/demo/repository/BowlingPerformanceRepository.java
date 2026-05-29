// ── BowlingPerformanceRepository.java ────────────────────────
package com.example.demo.repository;

import com.example.demo.entity.BowlingPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BowlingPerformanceRepository extends JpaRepository<BowlingPerformance, Long> {
    List<BowlingPerformance>     findByInningsId(Long inningsId);
    Optional<BowlingPerformance> findByPlayerIdAndInningsId(Long playerId, Long inningsId);
}