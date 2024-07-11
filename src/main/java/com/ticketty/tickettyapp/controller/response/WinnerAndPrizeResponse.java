package com.ticketty.tickettyapp.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
public class WinnerAndPrizeResponse {

    private Integer winnerUserId;
    private String winnerNickname;
    private Integer ticketId;
    private Integer prizeMoney;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp winningDate;
    private String emoji;

    public WinnerAndPrizeResponse() {

    }
}
