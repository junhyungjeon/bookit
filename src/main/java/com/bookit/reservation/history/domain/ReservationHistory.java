package com.bookit.reservation.history.domain;

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
@Table(name = "reservation_history")
public class ReservationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationHistoryType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    protected ReservationHistory() {
    }

    public ReservationHistory(Reservation reservation, ReservationHistoryType type) {
        this.reservation = reservation;
        this.type = type;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public ReservationHistoryType getType() { return type; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
