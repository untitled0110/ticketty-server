package com.ticketty.tickettyapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresentTicketCountAndPrizeMoneyResponse {

    private Integer ticketCount;
    private Integer prizeMoney;
}
