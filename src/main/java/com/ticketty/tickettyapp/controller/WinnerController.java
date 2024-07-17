package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.controller.response.WinnerHistoryResponse;
import com.ticketty.tickettyapp.service.WinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WinnerController {

    private final WinnerService winnerService;

    @GetMapping("/winners")
    public Response<WinnerAndPrizeResponse> getTodayWinnerAndPrize() {

        return Response.success(winnerService.getTodayWinnerAndPrize());
    }

    @GetMapping("/winnings")
    public Response<List<WinnerHistoryResponse>> getWinnerHistory(HttpServletRequest httpServletRequest,
                                                                  @RequestParam int page,
                                                                  @RequestParam int count) {

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        List<WinnerHistoryResponse> winnerHistory = winnerService.getWinnerHistoryByUserId(userId, page, count);
        return Response.success(winnerHistory);
    }
}
