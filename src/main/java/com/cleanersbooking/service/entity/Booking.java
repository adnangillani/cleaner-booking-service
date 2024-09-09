package com.cleanersbooking.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "booking_start_time")
    private LocalTime bookingStartTime;

    @Column(name = "booking_duration")
    private Integer bookingDuration;

    @ManyToMany
    @JoinTable(
            name = "cleaner_bookings",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "cleaner_id")
    )
    private List<Cleaner> cleaners = new ArrayList<>();
}
