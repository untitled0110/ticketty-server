package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.entity.TicketEntity;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import com.ticketty.tickettyapp.repository.TicketEntityRepository;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.repository.WinnerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketEntityRepository ticketEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final WinnerEntityRepository winnerEntityRepository;

    @Transactional
    public IssueTicketResponse createTicket(Integer userId) {

        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userId)));

        TicketEntity ticketEntity = TicketEntity.of(userEntity); // UserEntity를 사용하여 TicketEntity 생성
        ticketEntityRepository.save(ticketEntity); // TicketEntity 저장

        UserEntity user = ticketEntity.getUser();

        return new IssueTicketResponse(ticketEntity.getId(), user.getId());
    }

    @Transactional
//    @Scheduled(cron = "0 0 21 * * ?") // 매일 오후 9시에 실행
    @Scheduled(cron = "0 33 15 * * ?")
    public void selectWinningTicketAndCalculatePrize() {
        LocalDateTime yesterdayTenPM = LocalDateTime.now().minusDays(1).withHour(22).withMinute(0).withSecond(0);
        LocalDateTime todayNinePM = LocalDateTime.now().withHour(21).withMinute(0).withSecond(0);

        Timestamp startOfDay = Timestamp.valueOf(yesterdayTenPM);
        Timestamp endOfDay = Timestamp.valueOf(todayNinePM);

        List<TicketEntity> todayTickets = ticketEntityRepository.findByRegisteredAtBetween(startOfDay, endOfDay);
        int totalTickets = todayTickets.size();

        if (totalTickets > 0) {
            // 당첨금 계산
            int prizeAmount = totalTickets; // 티켓 1장당 1원
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
            winnerEntityRepository.save(winner);
        } else {
            System.out.println("당일 발급된 티켓이 없습니다.");
        }
    }

}
