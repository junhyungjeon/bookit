package com.bookit.reservation.slot.domain;

import com.bookit.service.domain.Service;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "reservation_slot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"service_id", "start_at"})
)
public class ReservationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private int capacity;

    @Version
    private Long version;

    private LocalDateTime lastReservationAt;

    protected ReservationSlot() {
    }

    public ReservationSlot(Service service, LocalDateTime startAt, LocalDateTime endAt, int capacity) {
        this.service = service;
        this.startAt = startAt;
        this.endAt = endAt;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public Service getService() { return service; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public int getCapacity() { return capacity; }

    public Long getVersion() { return version; }

    public void recordReservationAttempt() {
        this.lastReservationAt = LocalDateTime.now();
    }
}
