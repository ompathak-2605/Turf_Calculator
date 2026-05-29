package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/** A bowler's figures for one innings. */
@Entity
@Table(name = "bowling_performances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BowlingPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default private Integer legalDeliveries = 0;  // balls that count toward overs
    @Builder.Default private Integer ballsBowled     = 0;  // total inc. wides & no-balls
    @Builder.Default private Integer runsConceded    = 0;
    @Builder.Default private Integer wicketsTaken    = 0;  // does NOT count run-outs
    @Builder.Default private Integer maidens         = 0;
    @Builder.Default private Integer wides           = 0;
    @Builder.Default private Integer noBalls         = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    /** Economy rate (runs per over) */
    @Transient
    public double getEconomyRate() {
        if (legalDeliveries == null || legalDeliveries == 0) return 0.0;
        return (runsConceded * 6.0) / legalDeliveries;
    }

    /** Overs in cricket format: e.g. "3.2" means 3 overs and 2 balls */
    @Transient
    public String getOversBowled() {
        if (legalDeliveries == null) return "0.0";
        int ov = legalDeliveries / 6;
        int b  = legalDeliveries % 6;
        return ov + "." + b;
    }
}