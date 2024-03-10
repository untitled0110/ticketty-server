package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.service.WinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/winners")
public class WinnerController {

    private final WinnerService winnerService;

    @GetMapping
    public Response<WinnerAndPrizeResponse> getTodayWinnerAndPrize() {

        return Response.success(winnerService.getTodayWinnerAndPrize());

    }
}
