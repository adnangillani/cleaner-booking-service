package com.cleanersbooking.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "vehicles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_name")
    private String vehicleName;
}
