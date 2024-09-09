package com.cleanersbooking.service.service;

import com.cleanersbooking.service.config.AppConfig;
import com.cleanersbooking.service.dto.AvailableCleanerDto;
import com.cleanersbooking.service.dto.TimeSlotDto;
import com.cleanersbooking.service.entity.Booking;
import com.cleanersbooking.service.entity.Cleaner;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.repository.BookingRepository;
import com.cleanersbooking.service.repository.CleanerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AvailabilityServiceTest {

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private CleanerRepository cleanerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(appConfig.getWorkingStartTime()).thenReturn(LocalTime.of(8, 0));
        when(appConfig.getWorkingEndTime()).thenReturn(LocalTime.of(22, 0));
    }

    @Test
    void testGetAvailableCleanerDtoList_withValidInput() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        int duration = 2;

        List<Cleaner> cleaners = Arrays.asList(
                Cleaner.builder()
                        .cleanerId(1L)
                        .cleanerName("John Doe")
                        .build(),
                Cleaner.builder()
                        .cleanerId(2L)
                        .cleanerName("Jane Doe")
                        .build()
        );

        when(cleanerRepository.findAll()).thenReturn(cleaners);

        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByBookingDateAndCleanerId(any(), anyLong())).thenReturn(bookings);

        List<AvailableCleanerDto> availableCleanerDtos = availabilityService.getAvailableCleanerDtoList(date, startTime, duration);

        assertNotNull(availableCleanerDtos);
        assertEquals(2, availableCleanerDtos.size());
    }

    @Test
    void testValidateCleanerWorkingDay_withFriday_throwsValidationException() {
        LocalDate friday = LocalDate.of(2024, 9, 13); // This is a Friday

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            availabilityService.validateCleanerWorkingDay(friday);
        });

        assertEquals("Cleaners are not working on Fridays", exception.getMessage());
    }

    @Test
    void testValidateCleanerWorkingHours_outsideWorkingHours_throwsValidationException() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(7, 0); // Before working hours
        int duration = 2;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            availabilityService.validateCleanerWorkingHours(date, startTime, duration);
        });

        assertEquals("The provided time is outside of working hours i.e. 08:00 to 22:00", exception.getMessage());
    }

    @Test
    void testGetAvailableSlots_withBookings() {
        LocalDate date = LocalDate.now();

        List<Booking> bookings = new ArrayList<>();
        bookings.add(Booking.builder()
                .bookingId(1L)
                .bookingDate(date)
                .bookingStartTime(LocalTime.of(9, 0))
                .bookingDuration(2)
                .build());

        bookings.add(Booking.builder()
                .bookingId(2L)
                .bookingDate(date)
                .bookingStartTime(LocalTime.of(12, 0))
                .bookingDuration(2)
                .build());

        List<TimeSlotDto> availableSlots = availabilityService.getAvailableSlots(date, bookings);

        assertNotNull(availableSlots);
        assertEquals(2, availableSlots.size()); // Expected 2 available slots
    }

    @Test
    void testHasAvailableSlot_withMatchingSlot() {
        List<TimeSlotDto> slots = new ArrayList<>();
        slots.add(new TimeSlotDto(LocalDateTime.of(2024, 9, 5, 10, 0),
                LocalDateTime.of(2024, 9, 5, 12, 0)));

        LocalDateTime bookingStart = LocalDateTime.of(2024, 9, 5, 10, 0);
        LocalDateTime bookingEnd = LocalDateTime.of(2024, 9, 5, 12, 0);

        boolean result = availabilityService.hasAvailableSlot(slots, bookingStart, bookingEnd);

        assertTrue(result);
    }
}
