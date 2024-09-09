package com.cleanersbooking.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cleaners")
public class Cleaner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cleaner_id")
    private Long cleanerId;

    @Column(name = "cleaner_name")
    private String cleanerName;

    @ManyToMany(mappedBy = "cleaners")
    private List<Booking> bookings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
