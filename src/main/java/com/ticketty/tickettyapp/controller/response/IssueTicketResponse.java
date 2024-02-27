package com.ticketty.tickettyapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IssueTicketResponse {

    private Integer ticketId;
    private Integer userId;

}
