package com.ticketty.tickettyapp.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinnerHistoryResponse {

    private Integer winnerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp registeredAt;

    private String status;
    private Integer prizeMoney;
    private Integer userId;
}
