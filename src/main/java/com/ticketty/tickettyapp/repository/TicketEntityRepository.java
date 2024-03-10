package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TicketEntityRepository extends JpaRepository<TicketEntity, Integer> {

    List<TicketEntity> findByRegisteredAtBetween(Timestamp start, Timestamp end);


}
