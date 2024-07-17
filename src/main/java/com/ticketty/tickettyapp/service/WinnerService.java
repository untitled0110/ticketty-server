package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.WinnerAndPrizeResponse;
import com.ticketty.tickettyapp.controller.response.WinnerHistoryResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.WinnerStatus;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import com.ticketty.tickettyapp.repository.WinnerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WinnerService {

    private final WinnerEntityRepository winnerEntityRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${discord.webhook-url}")
    private String discordWebhookUrl;

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

    @Transactional
    public void updateWinnerStatus(Integer winnerId, Integer userId, WinnerStatus status) {

        WinnerEntity winner = winnerEntityRepository.findById(winnerId)
                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.WINNER_NOT_FOUND));

        if (!winner.getUser().getId().equals(userId)) {
            throw new TickettyAppApplicationException(ErrorCode.UNAUTHORIZED_USER, "User not authorized to update this winner status");
        }

        if (winner.getStatus() == status) {
            throw new TickettyAppApplicationException(ErrorCode.ALREADY_REGISTERED_STATUS, "Status is already " + status);
        }

        winner.setStatus(status);
        if (status == WinnerStatus.REQUEST_COMPLETED) {
            Timestamp requestedAt = Timestamp.from(Instant.now());
            winner.setRequestedAt(requestedAt);
            sendDiscordWebhook(winnerId, userId, requestedAt, winner.getPrizeMoney());
        }
        if (status == WinnerStatus.PAYMENT_COMPLETED) {
            winner.setPayedAt(Timestamp.from(Instant.now()));
        }
        winnerEntityRepository.save(winner);
    }

    private void sendDiscordWebhook(Integer winnerId, Integer userId, Timestamp requestedAt, Integer prizeMoney) {

        Map<String, Object> body = new HashMap<>();
        body.put("content", "입금요청");
        body.put("tts", false);

        Map<String, Object> field1 = new HashMap<>();
        field1.put("name", "Winner ID");
        field1.put("value", winnerId.toString());
        field1.put("inline", true);

        Map<String, Object> field2 = new HashMap<>();
        field2.put("name", "User ID");
        field2.put("value", userId.toString());
        field2.put("inline", true);

        Map<String, Object> field3 = new HashMap<>();
        field3.put("name", "Requested At");
        field3.put("value", requestedAt.toString());

        Map<String, Object> field4 = new HashMap<>();
        field4.put("name", "Prize Money");
        field4.put("value", prizeMoney.toString());

        Map<String, Object> embed = new HashMap<>();
        embed.put("fields", List.of(field1, field2, field3, field4));

        body.put("embeds", List.of(embed));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(discordWebhookUrl, request, String.class);
    }

}
