package com.ticketty.tickettyapp.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"tickets\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"tickets\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, Lazy 로딩 설정
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // 외래 키 설정
    private UserEntity user;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


    public static TicketEntity of(UserEntity userEntity) {
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setUser(userEntity);
        return ticketEntity;
    }

}