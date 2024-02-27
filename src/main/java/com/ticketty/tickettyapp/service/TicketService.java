package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.IssueTicketResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.entity.TicketEntity;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.repository.TicketEntityRepository;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketEntityRepository ticketEntityRepository;

    private final UserEntityRepository userEntityRepository;

    @Transactional
    public IssueTicketResponse createTicket(Integer userId) {

        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userId)));

        TicketEntity ticketEntity = TicketEntity.of(userEntity); // UserEntity를 사용하여 TicketEntity 생성
        ticketEntityRepository.save(ticketEntity); // TicketEntity 저장

        UserEntity user = ticketEntity.getUser();

        return new IssueTicketResponse(ticketEntity.getId(), user.getId());
    }

}
