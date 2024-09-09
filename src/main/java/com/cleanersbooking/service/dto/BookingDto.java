package com.cleanersbooking.service.dto;

import com.cleanersbooking.service.validation.groups.CreateBookingValidationGroup;
import com.cleanersbooking.service.validation.groups.UpdateBookingValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"cleaners"})
public class BookingDto {

    @JsonIgnore
    private Long bookingId;

    @NotNull(message = "Booking date is required.", groups = {CreateBookingValidationGroup.class, UpdateBookingValidationGroup.class})
    private LocalDate bookingDate;

    @NotNull(message = "Booking start time is required.", groups = {CreateBookingValidationGroup.class, UpdateBookingValidationGroup.class})
    private LocalTime bookingStartTime;

    @Min(value = 2, message = "Booking duration must be at least 2 hours.", groups = {CreateBookingValidationGroup.class, UpdateBookingValidationGroup.class})
    @Max(value = 4, message = "Booking duration cannot exceed 4 hours.", groups = {CreateBookingValidationGroup.class, UpdateBookingValidationGroup.class})
    private int bookingDuration;

    @NotNull(message = "Cleaner count is required.", groups = CreateBookingValidationGroup.class)
    @Min(value = 1, message = "At least 1 cleaner is required.", groups = {CreateBookingValidationGroup.class})
    @Max(value = 3, message = "No more than 3 cleaners are allowed.", groups = {CreateBookingValidationGroup.class})
    private int cleanerCount;

    private List<CleanerDto> cleaners;
}
