package com.ticketty.tickettyapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class WinnerAndPrizeResponse {

    private Integer winner_user_id;
    private Integer ticket_id;
    private Integer prize_money;

    public WinnerAndPrizeResponse() {

    }
}
