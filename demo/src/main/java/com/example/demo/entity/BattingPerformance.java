package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/** A batter's scorecard for one innings. */
@Entity
@Table(name = "batting_performances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BattingPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default private Integer runsScored     = 0;
    @Builder.Default private Integer ballsFaced     = 0;
    @Builder.Default private Integer fours          = 0;
    @Builder.Default private Integer sixes          = 0;

    private Integer battingPosition;      // 1 = opener, etc.

    @Builder.Default private Boolean isOut   = false;
    @Builder.Default private Boolean didBat  = true;

    @Enumerated(EnumType.STRING)
    private BallEvent.WicketType dismissalType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    /** Bowler who took the wicket (null for run-outs) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowler_id")
    private Player bowler;

    /** Fielder who took the catch (null if not a catch) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fielder_id")
    private Player fielder;

    @Transient
    public double getStrikeRate() {
        if (ballsFaced == null || ballsFaced == 0) return 0;
        return (runsScored * 100.0) / ballsFaced;
    }
}