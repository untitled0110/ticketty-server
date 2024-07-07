//package com.ticketty.tickettyapp.config;
//import com.ticketty.tickettyapp.model.entity.TicketEntity;
//import com.ticketty.tickettyapp.model.entity.UserEntity;
//import com.ticketty.tickettyapp.repository.TicketEntityRepository;
//import com.ticketty.tickettyapp.repository.UserEntityRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.Random;
//
//@Component
//@RequiredArgsConstructor
//public class TicketDataGenerator {
//
//    private final TicketEntityRepository ticketEntityRepository;
//    private final UserEntityRepository userEntityRepository;
//
//    @PostConstruct
//    @Transactional
//    public void generateTickets() {
//        List<UserEntity> users = userEntityRepository.findAll();
//        Random random = new Random();
//
//        for (int i = 0; i < 1000; i++) {
//            // 사용자 랜덤으로 선택
//            UserEntity user = users.get(random.nextInt(users.size()));
//
//            TicketEntity ticket = new TicketEntity(user);
//            ticketEntityRepository.save(ticket);
//        }
//    }
//}