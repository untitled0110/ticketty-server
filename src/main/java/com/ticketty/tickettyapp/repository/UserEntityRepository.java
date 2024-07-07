package com.ticketty.tickettyapp.repository;

import com.ticketty.tickettyapp.model.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    @Override
    Optional<UserEntity> findById(Integer id);

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByPhone(String phone);

    @Modifying
    @Query("UPDATE UserEntity u SET u.nickname = :nickname WHERE u.id = :userId")
    void updateNicknameById(@Param("userId") Integer userId, @Param("nickname") String nickname);

    @Modifying
    @Query("UPDATE UserEntity u SET u.phone = :phoneNumber WHERE u.id = :userId")
    void updatePhoneById(@Param("userId") Integer userId, @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE UserEntity u SET u.accountNumber = :accountNumber, u.bankName = :bankName, u.accountHolder = :accountHolder WHERE u.id = :userId")
    void updateAccountInfoById(@Param("userId") Integer userId, @Param("accountNumber") String accountNumber, @Param("bankName") String bankName, @Param("accountHolder") String accountHolder);

    @Modifying
    @Query("UPDATE UserEntity u SET u.emoji = :emoji WHERE u.id = :userId")
    void updateEmojiById(@Param("userId") Integer userId, @Param("emoji") String emoji);

    @Modifying
    @Query("UPDATE UserEntity u SET u.accountNumber = :accountNumber, u.bankName = :bankName, u.accountHolder = :accountHolder, u.accountRegisteredAt = :accountRegisteredAt WHERE u.id = :userId")
    void updateAccountInfo(
            @Param("userId") Integer userId,
            @Param("accountNumber") String accountNumber,
            @Param("bankName") String bankName,
            @Param("accountHolder") String accountHolder,
            @Param("accountRegisteredAt") Timestamp accountRegisteredAt
    );

    boolean existsByBankNameAndAccountNumber(String bankName, String accountNumber);


}
