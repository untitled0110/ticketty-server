package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.controller.response.PresentTicketCountAndPrizeMoneyResponse;
import com.ticketty.tickettyapp.controller.response.PresentUserTicketCountResponse;
import com.ticketty.tickettyapp.controller.response.UserTicketRankingResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.WinnerStatus;
import com.ticketty.tickettyapp.model.entity.TicketEntity;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import com.ticketty.tickettyapp.repository.TicketEntityRepository;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.repository.WinnerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketEntityRepository ticketEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final WinnerEntityRepository winnerEntityRepository;

    @Value("${prize.default-amount}")
    private int defaultPrizeAmount;

    @Transactional
    public IssueTicketResponse createTicket(Integer userId) {

        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userId)));

        TicketEntity ticketEntity = TicketEntity.of(userEntity); // UserEntity를 사용하여 TicketEntity 생성
        ticketEntityRepository.save(ticketEntity); // TicketEntity 저장

        UserEntity user = ticketEntity.getUser();

        return new IssueTicketResponse(ticketEntity.getId(), user.getId());
    }

@Transactional
@Scheduled(cron = "0 0 21 * * ?") // 매일 오후 9시에 실행
public void selectWinningTicketAndCalculatePrize() {
    LocalDateTime yesterdayTenPM = LocalDateTime.now().minusDays(1).withHour(22).withMinute(0).withSecond(0);
    LocalDateTime todayNinePM = LocalDateTime.now().withHour(21).withMinute(0).withSecond(0);

    Timestamp startOfDay = Timestamp.valueOf(yesterdayTenPM);
    Timestamp endOfDay = Timestamp.valueOf(todayNinePM);

    List<TicketEntity> todayTickets = ticketEntityRepository.findByRegisteredAtBetween(startOfDay, endOfDay);
    int totalTickets = todayTickets.size();

    LocalDateTime yesterdayStartOfDay = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
    LocalDateTime yesterdayEndOfDay = yesterdayStartOfDay.withHour(23).withMinute(59).withSecond(59);

    Timestamp startOfDay2 = Timestamp.valueOf(yesterdayStartOfDay);
    Timestamp endOfDay2 = Timestamp.valueOf(yesterdayEndOfDay);

    // 어제의 WinnerEntity 중 status가 WINNING_CANCELLED인 엔티티를 찾기
    List<WinnerEntity> cancelledWinners = winnerEntityRepository.findByRegisteredAtBetweenAndStatus(startOfDay2, endOfDay2, WinnerStatus.WINNING_CANCELLED);
    int cancelledPrizeAmount = cancelledWinners.stream().mapToInt(WinnerEntity::getPrizeMoney).sum();

    // 기본 당첨금 계산
//    int prizeAmount = totalTickets + defaultPrizeAmount;
//
//    List<WinnerEntity> cancelledWinners = winnerEntityRepository.findByRegisteredAtBetweenAndStatus(startOfDay, endOfDay, WinnerStatus.WINNING_CANCELLED);
//    for (WinnerEntity cancelledWinner : cancelledWinners) {
//        prizeAmount += cancelledWinner.getPrizeMoney(); // 취소된 당첨자의 prizeMoney를 현재 prizeAmount에 추가
//    }

    if (totalTickets > 0) {
        // 당첨금 계산
        int prizeAmount = totalTickets + defaultPrizeAmount + cancelledPrizeAmount; // 티켓 1장당 1원 + 기본 금액 + 취소된 당첨금
        System.out.println("당첨금: " + prizeAmount + "원");

        // 당첨 티켓 선정
        Random random = new Random();
        TicketEntity winningTicket = todayTickets.get(random.nextInt(totalTickets));
        System.out.println("당첨 티켓: " + winningTicket.getId());

        // 당첨자 정보 저장
        WinnerEntity winner = new WinnerEntity();
        winner.setTicket(winningTicket);
        winner.setPrizeMoney(prizeAmount);
        winner.setUser(winningTicket.getUser()); // 티켓 소유자를 당첨자로 설정
        winner.setStatus(WinnerStatus.BEFORE_REQUEST);
        winnerEntityRepository.save(winner);
    } else {
        System.out.println("당일 발급된 티켓이 없습니다.");
    }
}

    private Timestamp[] setDayBoundaries(LocalDateTime currentTime) {
        LocalDateTime startOfDay;
        LocalDateTime endOfDay = LocalDateTime.now();

        if (currentTime.getHour() < 22) {
            startOfDay = LocalDateTime.now().minusDays(1).withHour(22).withMinute(0).withSecond(0);
        } else {
            startOfDay = LocalDateTime.now().withHour(22).withMinute(0).withSecond(0);
        }

        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);

        return new Timestamp[]{startTimestamp, endTimestamp};
    }

    @Transactional(readOnly = true)
    public PresentTicketCountAndPrizeMoneyResponse getPresentTicketCountAndPrizeMoney() {
        LocalDateTime currentTime = LocalDateTime.now();

//        if (currentTime.getHour() == 21) {
//            return new PresentTicketCountAndPrizeMoneyResponse(0, 0);
//        }

        Timestamp[] boundaries = setDayBoundaries(currentTime);
        Timestamp startTimestamp = boundaries[0];
        Timestamp endTimestamp = boundaries[1];

        int ticketCount = ticketEntityRepository.countByRegisteredAtBetween(startTimestamp, endTimestamp);
        int prizeMoney = ticketCount + defaultPrizeAmount;

        return new PresentTicketCountAndPrizeMoneyResponse(ticketCount, prizeMoney);
    }

    @Transactional(readOnly = true)
    public PresentUserTicketCountResponse getPresentUserTicketCount(Integer userId) {
        LocalDateTime currentTime = LocalDateTime.now();

//        if (currentTime.getHour() == 21) {
//            return new PresentUserTicketCountResponse(0);
//        }

        Timestamp[] boundaries = setDayBoundaries(currentTime);
        Timestamp startTimestamp = boundaries[0];
        Timestamp endTimestamp = boundaries[1];

        int ticketCount = ticketEntityRepository.countByUser_IdAndRegisteredAtBetween(userId, startTimestamp, endTimestamp);

        return new PresentUserTicketCountResponse(ticketCount);
    }


    @Transactional(readOnly = true)
    public List<UserTicketRankingResponse> getTicketIssuanceRanking() {
        LocalDateTime currentTime = LocalDateTime.now();

        Timestamp[] boundaries = setDayBoundaries(currentTime);
        Timestamp startTimestamp = boundaries[0];
        Timestamp endTimestamp = boundaries[1];

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Object[]> topUsers = ticketEntityRepository.findTop10UsersByTicketCount(startTimestamp, endTimestamp, pageRequest);

        return topUsers.stream()
                .map(record -> new UserTicketRankingResponse(
                        (Integer) record[0],  // userId
                        (Long) record[3],     // ticketCount
                        (String) record[1],   // nickname
                        (String) record[2],   // emoji
                        (Timestamp) record[4] // latestCreatedAt
                ))
                .collect(Collectors.toList());
    }

}