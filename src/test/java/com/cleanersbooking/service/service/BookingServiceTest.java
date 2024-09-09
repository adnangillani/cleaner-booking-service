package com.cleanersbooking.service.service;

import com.cleanersbooking.service.dto.BookingDto;
import com.cleanersbooking.service.entity.Booking;
import com.cleanersbooking.service.entity.Cleaner;
import com.cleanersbooking.service.entity.Vehicle;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test case
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateBooking_Success() {
        // Given
        BookingDto bookingDto = createSampleBookingDto();
        Cleaner cleaner = createSampleCleaner();
        Booking booking = createSampleBooking();

        // Mocking the service responses
        when(availabilityService.getAvailableCleanerList(any(), any(), anyInt()))
                .thenReturn(Collections.singletonList(cleaner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingDto createdBooking = bookingService.createBooking(bookingDto);

        // Then
        assertNotNull(createdBooking);
        assertEquals(bookingDto.getBookingDate(), createdBooking.getBookingDate());
        verify(availabilityService, times(1)).getAvailableCleanerList(any(), any(), anyInt());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_NoAvailableCleaners() {
        // Given
        BookingDto bookingDto = createSampleBookingDto();

        // Mocking no available cleaners
        when(availabilityService.getAvailableCleanerList(any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        // Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto));

        assertEquals("No available cleaners for this time.", exception.getMessage());
        verify(availabilityService, times(1)).getAvailableCleanerList(any(), any(), anyInt());
    }

    @Test
    void testUpdateBooking_BookingNotFound() {
        // Given
        BookingDto bookingDto = createSampleBookingDto();

        // Mocking no existing booking
        when(bookingRepository.findByBookingDateAndCleanerId(any(LocalDate.class), anyLong()))
                .thenReturn(Collections.emptyList()); // Return an empty list for the mock

        // Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(1L, bookingDto));

        assertEquals("Booking not found", exception.getMessage());
    }


    private BookingDto createSampleBookingDto() {
        return BookingDto.builder()
                .bookingDate(LocalDate.now().plusDays(1))
                .bookingStartTime(LocalTime.of(10, 0))
                .bookingDuration(2)
                .cleanerCount(1)
                .build();
    }

    private Cleaner createSampleCleaner() {
        Cleaner cleaner = new Cleaner();
        cleaner.setCleanerId(1L);
        cleaner.setCleanerName("John Doe");

        // Create a Vehicle and associate it with the Cleaner
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(100L);
        vehicle.setVehicleName("Van 1");

        cleaner.setVehicle(vehicle); // Associate the vehicle with the cleaner
        return cleaner;
    }


    private Booking createSampleBooking() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setBookingStartTime(LocalTime.of(10, 0));
        booking.setBookingDuration(2);
        return booking;
    }
}
