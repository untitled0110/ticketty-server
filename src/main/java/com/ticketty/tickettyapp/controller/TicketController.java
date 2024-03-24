package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.controller.response.PresentTicketCountAndPrizeMoneyResponse;
import com.ticketty.tickettyapp.controller.response.PresentUserTicketCountResponse;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Response<IssueTicketResponse> issueTicket(HttpServletRequest httpServletRequest) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.getHour() == 21) {
            throw new TickettyAppApplicationException(ErrorCode.NOT_TICKET_ISSUE_TIME);
        }

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        IssueTicketResponse issueTicketResponse = ticketService.createTicket(userId);

        return Response.success(issueTicketResponse);
    }


    @GetMapping("/quantity")
    public Response<PresentTicketCountAndPrizeMoneyResponse> getTicketCountAndPrizeMoney() {

        PresentTicketCountAndPrizeMoneyResponse presentTicketCountAndPrizeMoneyResponse = ticketService.getPresentTicketCountAndPrizeMoney();
        return Response.success(presentTicketCountAndPrizeMoneyResponse);
    }

    @GetMapping("/user-quantity")
    public Response<PresentUserTicketCountResponse> getUserTicketCount(HttpServletRequest httpServletRequest) {

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        PresentUserTicketCountResponse presentUserTicketCountResponse = ticketService.getPresentUserTicketCount(userId);
        return Response.success(presentUserTicketCountResponse);
    }
}
