package com.cleanersbooking.service.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableCleanerDto {
    private Long cleanerId;
    private String cleanerName;
    private List<TimeSlotDto> availableSlots;
    @JsonIgnore
    private List<TimeSlotDto> bookings;
}