package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.WinnerStatus;
import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WinnerEntityRepository extends JpaRepository<WinnerEntity, Integer> {

    // 오늘 날짜로 등록된 데이터 조회
//    List<WinnerEntity> findByRegisteredAtBetween(Timestamp startOfDay, Timestamp endOfDay);

    @Query("SELECT w FROM WinnerEntity w JOIN FETCH w.user WHERE w.registeredAt BETWEEN :startOfDay AND :endOfDay")
    List<WinnerEntity> findByRegisteredAtBetween(@Param("startOfDay") Timestamp startOfDay, @Param("endOfDay") Timestamp endOfDay);

    @Query("SELECT w FROM WinnerEntity w WHERE w.user.id = :userId ORDER BY w.registeredAt DESC")
    Page<WinnerEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    List<WinnerEntity> findByRegisteredAtBetweenAndStatus(Timestamp startOfDay, Timestamp endOfDay, WinnerStatus status);

//    @Query("SELECT w FROM WinnerEntity w WHERE w.registeredAt BETWEEN :startOfDay AND :endOfDay AND w.status = :status")
//    List<WinnerEntity> findByRegisteredAtBetweenAndStatus(@Param("startOfDay") Timestamp startOfDay, @Param("endOfDay") Timestamp endOfDay, @Param("status") WinnerStatus status);

    //List<WinnerEntity> findByRegisteredAtBetweenAndStatus(Timestamp start, Timestamp end, WinnerStatus status);

}
