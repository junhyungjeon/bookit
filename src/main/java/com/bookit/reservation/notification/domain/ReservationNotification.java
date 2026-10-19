package com.bookit.reservation.notification.domain;

import com.bookit.reservation.domain.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_notification")
public class ReservationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ReservationNotification() {
    }

    public ReservationNotification(Reservation reservation) {
        this.reservation = reservation;
        this.type = NotificationType.RESERVATION_REMINDER;
        this.status = NotificationStatus.SCHEDULED;
        this.scheduledAt = reservation.getSlot().getStartAt().minusDays(1);
        this.createdAt = LocalDateTime.now();
    }

    public void send() {
        if (status == NotificationStatus.SCHEDULED) {
            this.status = NotificationStatus.SENT;
            this.sentAt = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (status == NotificationStatus.SCHEDULED) {
            this.status = NotificationStatus.CANCELLED;
        }
    }

    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getSentAt() { return sentAt; }
}
