package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.controller.response.WinnerHistoryResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import com.ticketty.tickettyapp.repository.WinnerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WinnerService {

    private final WinnerEntityRepository winnerEntityRepository;

    public WinnerAndPrizeResponse getTodayWinnerAndPrize() {
        // 오늘 날짜의 시작과 끝을 Timestamp 형식으로 변환
        Timestamp startOfDay;
        Timestamp endOfDay;

        // 현재 시간이 22시 이전인 경우
        if (LocalTime.now().isBefore(LocalTime.of(22, 0))) {
            startOfDay = Timestamp.valueOf(LocalDate.now().minusDays(1).atTime(LocalTime.of(21, 0))); // 어제 오후 9시
            endOfDay = Timestamp.valueOf(LocalDate.now().atTime(LocalTime.of(20, 59, 59))); // 오늘 오후 8시 59분 59초
        } else { // 현재 시간이 22시 이후인 경우
            startOfDay = Timestamp.valueOf(LocalDate.now().atTime(LocalTime.of(21, 0))); // 오늘 오후 9시
            endOfDay = Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(LocalTime.of(20, 59, 59))); // 내일 오후 8시 59분 59초
        }

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
            response.setWinnerUserId(winner.getUser().getId());
            response.setWinnerNickname(winner.getUser().getNickname());
            response.setTicketId(winner.getTicket().getId());
            response.setPrizeMoney(winner.getPrizeMoney());
            response.setWinningDate(winner.getRegisteredAt());
            response.setEmoji(winner.getUser().getEmoji());
        } else {
            throw new TickettyAppApplicationException(ErrorCode.WINNER_NOT_FOUND);
        }
        return response;
    }


    public List<WinnerHistoryResponse> getWinnerHistoryByUserId(Integer userId, int page, int count) {
        Pageable pageable = PageRequest.of(page, count);
        List<WinnerEntity> winners = winnerEntityRepository.findByUserId(userId, pageable).getContent();
        return winners.stream().map(winner -> new WinnerHistoryResponse(
                winner.getId(),
                winner.getRegisteredAt(),
                winner.getStatus().name(),
                winner.getPrizeMoney(),
                winner.getUser().getId()
        )).collect(Collectors.toList());
    }

}
