package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.TicketCountResponse;
import com.ticketty.tickettyapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        IssueTicketResponse issueTicketResponse = ticketService.createTicket(userId);

        return Response.success(issueTicketResponse);
    }

    @GetMapping("/quantity")
    public Response<TicketCountResponse> getTicketCount() {

        int ticketCount = ticketService.getTicketCount();
        return Response.success(new TicketCountResponse(ticketCount));
    }

    @GetMapping("/user-quantity")
    public Response<TicketCountResponse> getUserTicketCount(HttpServletRequest httpServletRequest) {

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        int ticketCount = ticketService.getUserTicketCount(userId);
        return Response.success(new TicketCountResponse(ticketCount));
    }
}
