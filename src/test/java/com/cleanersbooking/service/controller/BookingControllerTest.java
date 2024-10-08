package com.cleanersbooking.service.controller;


import com.cleanersbooking.service.dto.BookingDto;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateBooking_Success() throws Exception {
        // Given: BookingDto to send with the request
        BookingDto bookingDto = BookingDto.builder()
                .bookingDate(LocalDate.of(2024, 9, 10))
                .bookingStartTime(LocalTime.of(9, 0))
                .bookingDuration(2)
                .cleanerCount(2)
                .build();

        // Given: Expected response from service
        BookingDto createdBooking = BookingDto.builder()
                .bookingDate(LocalDate.of(2024, 9, 10))
                .bookingStartTime(LocalTime.of(9, 0))
                .bookingDuration(2)
                .cleanerCount(2)
                .build();

        // Mocking service behavior
        when(bookingService.createBooking(any(BookingDto.class))).thenReturn(createdBooking);

        // When: Making POST request
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())  // Then: Status should be 201 CREATED
                .andExpect(jsonPath("$.bookingDate").value("2024-09-10"))
                .andExpect(jsonPath("$.bookingStartTime").value("09:00:00"))
                .andExpect(jsonPath("$.cleanerCount").value(2));

        // Verify that the bookingService.createBooking method was called once
        verify(bookingService, times(1)).createBooking(any(BookingDto.class));
    }

    @Test
    public void testUpdateBooking_NotEnoughCleaners() throws Exception {
        // Given: BookingDto to send with the update request
        BookingDto bookingDto = BookingDto.builder()
                .bookingDate(LocalDate.of(2024, 9, 10))
                .bookingStartTime(LocalTime.of(14, 0))
                .bookingDuration(4)
                .cleanerCount(3)
                .build();

        // Mocking service behavior for ValidationException
        doThrow(new ValidationException(2L, "Not enough available cleaners for this time."))
                .when(bookingService).updateBooking(anyLong(), any(BookingDto.class));

        // When: Making PUT request
        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())  // Then: Status should be 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Not enough available cleaners for this time."));

        // Verify that the bookingService.updateBooking method was called once
        verify(bookingService, times(1)).updateBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    public void testUpdateBooking_Success() throws Exception {
        // Given: BookingDto with updated details to send with the request
        BookingDto bookingDto = BookingDto.builder()
                .bookingDate(LocalDate.of(2024, 9, 15))
                .bookingStartTime(LocalTime.of(10, 0))
                .bookingDuration(4)
                .cleanerCount(2)
                .build();

        // Given: Expected response from service with updated booking details
        BookingDto updatedBooking = BookingDto.builder()
                .bookingDate(LocalDate.of(2024, 9, 15))
                .bookingStartTime(LocalTime.of(10, 0))
                .bookingDuration(4)
                .cleanerCount(2)
                .build();

        // Mocking service behavior
        when(bookingService.updateBooking(anyLong(), any(BookingDto.class))).thenReturn(updatedBooking);

        // When: Making PUT request to update booking
        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())  // Then: Status should be 200 OK
                .andExpect(jsonPath("$.bookingDate").value("2024-09-15"))
                .andExpect(jsonPath("$.bookingStartTime").value("10:00:00"))
                .andExpect(jsonPath("$.cleanerCount").value(2));

        // Verify that the bookingService.updateBooking method was called once
        verify(bookingService, times(1)).updateBooking(anyLong(), any(BookingDto.class));
    }

}
