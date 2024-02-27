package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Response<IssueTicketResponse> issueTicket(HttpServletRequest httpServletRequest) {

//        String userIdString = (String) httpServletRequest.getAttribute("userId");
//        Integer userId = Integer.valueOf(userIdString);

        Integer userId = Integer.parseInt(httpServletRequest.getAttribute("userId").toString());
        IssueTicketResponse issueTicketResponse = ticketService.createTicket(userId);

        return Response.success(issueTicketResponse);
    }
}
