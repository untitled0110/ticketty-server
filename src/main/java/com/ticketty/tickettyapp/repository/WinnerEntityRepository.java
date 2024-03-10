package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WinnerEntityRepository extends JpaRepository<WinnerEntity, Integer> {

    // 오늘 날짜로 등록된 데이터 조회
    List<WinnerEntity> findByRegisteredAtBetween(Timestamp startOfDay, Timestamp endOfDay);

}
