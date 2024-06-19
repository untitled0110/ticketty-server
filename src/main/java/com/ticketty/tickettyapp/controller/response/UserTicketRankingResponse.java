package com.ticketty.tickettyapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTicketRankingResponse {
    private Integer userId;
    private Long ticketCount;
    private String nickname;
}