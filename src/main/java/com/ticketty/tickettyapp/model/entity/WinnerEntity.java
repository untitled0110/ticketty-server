package com.ticketty.tickettyapp.model.entity;

import com.ticketty.tickettyapp.model.WinnerStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"winners\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"winners\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class WinnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false, unique = true)
    private TicketEntity ticket;

    @Column(name = "prize_money", nullable = false)
    private Integer prizeMoney;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WinnerStatus status;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @Column(name = "payed_at")
    private Timestamp payedAt;

    @Column(name = "canceled_at")
    private Timestamp canceledAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
        if (this.status == null) {
            this.status = WinnerStatus.BEFORE_REQUEST;
        }
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }
}
