package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.WinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WinnerEntityRepository extends JpaRepository<WinnerEntity, Integer> {

}
