package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WinnerEntityRepository extends JpaRepository<WinnerEntity, Integer> {

    // 오늘 날짜로 등록된 데이터 조회
    List<WinnerEntity> findByRegisteredAtBetween(Timestamp startOfDay, Timestamp endOfDay);

//    default List<WinnerEntity> findByRegisteredAtToday() {
//        // 오늘 날짜의 시작과 끝을 Timestamp 형식으로 변환
//        Timestamp startOfDay = Timestamp.valueOf(LocalDate.now().atStartOfDay());
//        Timestamp endOfDay = Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1));
//        // 해당 기간 내에 등록된 데이터 조회
//        return findByRegisteredAtBetween(startOfDay, endOfDay);
//    }

//    @Query("SELECT w FROM WinnerEntity w JOIN FETCH w.user WHERE w.registeredAt BETWEEN :startOfDay AND :endOfDay")
//    List<WinnerEntity> findByRegisteredAtToday(@Param("startOfDay") Timestamp startOfDay, @Param("endOfDay") Timestamp endOfDay);
}
