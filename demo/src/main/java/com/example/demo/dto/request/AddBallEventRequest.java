package com.example.demo.dto.request;

import com.example.demo.entity.BallEvent;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AddBallEventRequest {

    private Long    inningsId;     // which innings this ball belongs to
    private Long    batterId;      // who is batting
    private Long    nonStrikerId;  // batter at the other end
    private Long    bowlerId;      // who is bowling
    private Long    fielderId;     // only needed for catches/run outs

    private Integer runs;          // runs scored off the bat (0–6)

    private Boolean isWide;
    private Boolean isNoBall;
    private Boolean isBye;
    private Boolean isLegBye;
    private Boolean isWicket;

    private BallEvent.WicketType wicketType; // BOWLED, CAUGHT, RUN_OUT etc.
}