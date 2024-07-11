package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.TicketEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TicketEntityRepository extends JpaRepository<TicketEntity, Integer> {

    List<TicketEntity> findByRegisteredAtBetween(Timestamp start, Timestamp end);

    int countByRegisteredAtBetween(Timestamp start, Timestamp end);

    int countByUser_IdAndRegisteredAtBetween(Integer userId, Timestamp start, Timestamp end);

    @Query("SELECT t.user.id, t.user.nickname, t.user.emoji, COUNT(t) as ticketCount, MAX(t.registeredAt) as latestCreatedAt " +
            "FROM TicketEntity t " +
            "WHERE t.registeredAt BETWEEN :start AND :end " +
            "GROUP BY t.user.id, t.user.nickname, t.user.emoji " +
            "ORDER BY ticketCount DESC, MAX(t.registeredAt) DESC")
    List<Object[]> findTop10UsersByTicketCount(Timestamp start, Timestamp end, Pageable pageable);

}
