package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "innings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer inningsNumber;   // 1 or 2

    @Builder.Default
    private Integer totalRuns = 0;

    @Builder.Default
    private Integer totalWickets = 0;

    @Builder.Default
    private Integer extras = 0;      // sum of all extras

    @Builder.Default
    private Integer wides = 0;

    @Builder.Default
    private Integer noBalls = 0;

    @Builder.Default
    private Integer byes = 0;

    @Builder.Default
    private Integer legByes = 0;

    @Builder.Default
    private Integer ballsBowled = 0; // legal deliveries only

    @Builder.Default
    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @JsonBackReference("match-innings")
    private Match match;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batting_team_id")
    private Team battingTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowling_team_id")
    private Team bowlingTeam;

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BattingPerformance> battingPerformances;

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BowlingPerformance> bowlingPerformances;

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BallEvent> ballEvents;

    /** Human-readable score: e.g. "84/3 (8.2 ov)" */
    @Transient
    public String getScoreString() {
        int overs = ballsBowled / 6;
        int balls = ballsBowled % 6;
        return totalRuns + "/" + totalWickets + " (" + overs + "." + balls + " ov)";
    }
}