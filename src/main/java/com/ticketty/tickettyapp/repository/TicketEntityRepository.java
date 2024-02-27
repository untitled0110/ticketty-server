package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketEntityRepository extends JpaRepository<TicketEntity, Integer> {


}


