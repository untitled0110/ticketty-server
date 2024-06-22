package com.ticketty.tickettyapp.model;

import com.ticketty.tickettyapp.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class User {
    private Integer id;
    private String email;
    private String nickname;
    private String password;
    private String phone;
    private String accountNumber;
    private String bankName;
    private String accountHolder;
    private String emoji;
    private UserRole userRole;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    // Entity를 DTO로(User 객체) 변환
    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getPassword(),
                entity.getPhone(),
                entity.getAccount_number(),
                entity.getBank_name(),
                entity.getAccount_holder(),
                entity.getEmoji(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}