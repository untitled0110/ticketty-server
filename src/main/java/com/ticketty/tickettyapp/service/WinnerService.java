package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import com.ticketty.tickettyapp.repository.WinnerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WinnerService {

    private final WinnerEntityRepository winnerEntityRepository;

    public WinnerAndPrizeResponse getTodayWinnerAndPrize() {
        // 오늘 날짜의 시작과 끝을 Timestamp 형식으로 변환
        Timestamp startOfDay = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay().minusSeconds(1));

        // 오늘의 날짜로 저장된 당첨자 정보를 가져옴
        List<WinnerEntity> todayWinners = winnerEntityRepository.findByRegisteredAtBetween(startOfDay, endOfDay);

        // 리턴할 응답 객체 생성
        WinnerAndPrizeResponse response = createWinnerAndPrizeResponse(todayWinners);

        return response;
    }

    private WinnerAndPrizeResponse createWinnerAndPrizeResponse(List<WinnerEntity> todayWinners) {
        WinnerAndPrizeResponse response = new WinnerAndPrizeResponse();

        if (!todayWinners.isEmpty()) {
            WinnerEntity winner = todayWinners.get(0);
            response.setWinner_user_id(winner.getUser().getId());
            response.setTicket_id(winner.getTicket().getId());
            response.setPrize_money(winner.getPrizeMoney());
        } else {
            throw new TickettyAppApplicationException(ErrorCode.WINNER_NOT_FOUND);
        }
        return response;
    }
}
