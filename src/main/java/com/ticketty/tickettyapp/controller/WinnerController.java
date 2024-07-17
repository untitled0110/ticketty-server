package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.WinnerStatusUpdateRequest;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.controller.response.WinnerHistoryResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.WinnerStatus;
import com.ticketty.tickettyapp.service.WinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WinnerController {

    private final WinnerService winnerService;

    @GetMapping("/winner/today")
    public Response<WinnerAndPrizeResponse> getTodayWinnerAndPrize() {

        return Response.success(winnerService.getTodayWinnerAndPrize());
    }

    @GetMapping("/winners")
    public Response<List<WinnerHistoryResponse>> getWinnerHistory(HttpServletRequest httpServletRequest,
                                                                  @RequestParam int page,
                                                                  @RequestParam int count) {

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        List<WinnerHistoryResponse> winnerHistory = winnerService.getWinnerHistoryByUserId(userId, page, count);
        return Response.success(winnerHistory);
    }

    @PatchMapping("/winners/{winnerId}/status")
    public Response<Void> updateWinnerStatus(
            HttpServletRequest httpServletRequest,
            @PathVariable Integer winnerId,
            @Valid @RequestBody WinnerStatusUpdateRequest statusUpdateRequest,
            Errors errors) {

        if (errors.hasErrors()) {
            String errorMessage = Objects.requireNonNull(errors.getFieldError("status")).getDefaultMessage();
            throw new TickettyAppApplicationException(ErrorCode.INVALID_STATUS_VALUE, (errorMessage));
        }

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        WinnerStatus status = WinnerStatus.valueOf(statusUpdateRequest.getStatus());
        winnerService.updateWinnerStatus(winnerId, userId, status);
        return Response.success(null);
    }

}
