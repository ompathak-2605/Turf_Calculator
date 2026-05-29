package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Holds every rule that can vary between turf matches.
 * All fields are nullable — null means "use the standard cricket default".
 *
 * Examples of flexibility:
 *   totalOvers = 6        → 6-over box cricket
 *   wideBallRuns = 2      → stricter wide penalty
 *   wicketsToFinish = 2   → match ends when 2 wickets fall (per team)
 *   boundaryRules = false → no boundaries, just run it
 *   fourRuns = 6          → boundary is worth 6 in some turf formats
 */
@Entity
@Table(name = "match_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchRules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalOvers;         // e.g. 6, 8, 10, 12, 20
    private Integer playersPerTeam;     // e.g. 6, 8, 11

    private Integer wideBallRuns;       // default 1 — extra runs added for a wide
    private Integer noBallRuns;         // default 1 — extra runs added for a no-ball
    private Boolean freeHitOnNoBall;    // is the next ball a free hit?

    private Integer maxOversPerBowler;  // null = no restriction

    /**
     * How many wickets finish the innings?
     * null = all out (playersPerTeam - 1)
     * 2    = match ends when 2 wickets fall (common in turf)
     */
    private Integer wicketsToFinish;

    private Boolean boundaryRules;      // are 4s and 6s counted?
    private Integer fourRuns;           // default 4
    private Integer sixRuns;            // default 6

    @Column(length = 1000)
    private String customNotes;         // free-text for any other local rules
}