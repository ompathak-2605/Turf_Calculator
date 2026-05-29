package com.example.demo.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateMatchRequest {

    // Match details
    private String    name;
    private String    venue;
    private LocalDate date;

    // Team names
    private String team1Name;
    private String team2Name;

    // Rules (all optional — defaults applied in MatchService)
    private Integer totalOvers;
    private Integer playersPerTeam;
    private Integer wideBallRuns;
    private Integer noBallRuns;
    private Boolean freeHitOnNoBall;
    private Integer maxOversPerBowler;
    private Integer wicketsToFinish;
    private Boolean boundaryRules;
    private Integer fourRuns;
    private Integer sixRuns;
    private String  customNotes;
}