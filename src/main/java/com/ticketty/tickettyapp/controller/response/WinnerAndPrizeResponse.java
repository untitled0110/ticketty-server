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

    private Integer winner_user_id;
    private String winner_nickname;
    private Integer ticket_id;
    private Integer prize_money;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp winning_date;

    public WinnerAndPrizeResponse() {

    }
}
