package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

/**
 * A single turf cricket match.
 * Each match owns its own MatchRules, so every game can have different over counts,
 * player limits, wide/no-ball penalties, etc.
 */
@Entity
@Table(name = "matches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;     // e.g. "Friday Night T6"
    private String venue;    // e.g. "Green Turf, Salt Lake"
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    /** Flexible, per-match rules — stored in a separate table */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rules_id")
    private MatchRules rules;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("match-teams")
    private List<Team> teams;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("match-innings")
    private List<Innings> innings;

    public enum MatchStatus {
        UPCOMING, IN_PROGRESS, COMPLETED, ABANDONED
    }
}