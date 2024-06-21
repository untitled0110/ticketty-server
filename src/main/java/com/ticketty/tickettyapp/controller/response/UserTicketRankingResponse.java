package com.ticketty.tickettyapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTicketRankingResponse {
    private Integer userId;
    private Long ticketCount;
    private String nickname;
    private Timestamp latestCreatedAt;
}