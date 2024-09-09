package com.cleanersbooking.service.controller;

import com.cleanersbooking.service.dto.AvailableCleanerDto;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.exception.handler.GlobalExceptionHandler;
import com.cleanersbooking.service.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AvailabilityControllerTest {

    @InjectMocks
    private AvailabilityController availabilityController;

    @Mock
    private AvailabilityService availabilityService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Include the GlobalExceptionHandler
                .build();
    }

    @Test
    void testCheckCleanersAvailabilityByDate_withDateTimeAndDuration_returnsAvailableCleanersForGivenTime() throws Exception {
        LocalDate date = LocalDate.of(2024, 9, 5);
        LocalTime startTime = LocalTime.of(10, 0);
        int duration = 2;

        List<AvailableCleanerDto> availableCleanerDtos = new ArrayList<>();
        availableCleanerDtos.add(new AvailableCleanerDto(1L, "John Doe", new ArrayList<>(), new ArrayList<>()));

        when(availabilityService.getAvailableCleanerDtoList(date, startTime, duration))
                .thenReturn(availableCleanerDtos);

        mockMvc.perform(get("/api/availability")
                        .param("date", date.toString())
                        .param("startTime", startTime.toString())
                        .param("duration", String.valueOf(duration))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cleanerId").value(1L))
                .andExpect(jsonPath("$[0].cleanerName").value("John Doe"));
    }

    @Test
    void testCheckCleanersAvailabilityByDate_onlyDateProvided_returnsAvailableCleanersWithTimes() throws Exception {
        LocalDate date = LocalDate.of(2024, 9, 5);

        List<AvailableCleanerDto> availableCleanerDtos = new ArrayList<>();
        availableCleanerDtos.add(new AvailableCleanerDto(1L, "John Doe", new ArrayList<>(), new ArrayList<>()));

        when(availabilityService.getAvailableCleanerDtoList(date, null, null))
                .thenReturn(availableCleanerDtos);

        mockMvc.perform(get("/api/availability")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cleanerId").value(1L))
                .andExpect(jsonPath("$[0].cleanerName").value("John Doe"));
    }


    @Test
    void testCheckCleanersAvailabilityByDate_withInvalidDate() throws Exception {
        LocalDate friday = LocalDate.of(2024, 9, 6); // Friday

        // Simulate the service throwing the ValidationException
        when(availabilityService.getAvailableCleanerDtoList(friday, null, null))
                .thenThrow(new ValidationException(2L, "Cleaners are not working on Fridays"));

        // Perform the request and log the output
        mockMvc.perform(get("/api/availability")
                        .param("date", friday.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(2L))
                .andExpect(jsonPath("$.message").value("Cleaners are not working on Fridays"));
    }

}
