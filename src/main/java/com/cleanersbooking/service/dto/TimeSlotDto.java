package com.cleanersbooking.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@Builder
@Getter
@Setter
public class TimeSlotDto {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public String toString() {
        return "Available slot from " + startTime + " to " + endTime;
    }
}