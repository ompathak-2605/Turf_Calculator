package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * One delivery — the single source of truth for every ball bowled.
 * All batting and bowling stats are derived from these records.
 */
@Entity
@Table(name = "ball_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer overNumber;      // 0-indexed (over 1 = overNumber 0)
    private Integer ballNumber;      // ball within the over (1–6 for legal balls)
    private Integer runs;            // runs scored off the bat only
    private Integer totalRuns;       // runs + extras on this ball

    @Builder.Default private Boolean isWicket  = false;
    @Builder.Default private Boolean isWide    = false;
    @Builder.Default private Boolean isNoBall  = false;
    @Builder.Default private Boolean isBye     = false;
    @Builder.Default private Boolean isLegBye  = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WicketType wicketType = WicketType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innings_id")
    private Innings innings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batter_id")
    private Player batter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "non_striker_id")
    private Player nonStriker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowler_id")
    private Player bowler;

    /** Fielder who took catch or ran out the batter */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fielder_id")
    private Player fielder;

    public enum WicketType {
        BOWLED, CAUGHT, RUN_OUT, LBW, STUMPED, HIT_WICKET, NONE
    }
}
