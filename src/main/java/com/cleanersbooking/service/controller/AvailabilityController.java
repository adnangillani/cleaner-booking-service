package com.cleanersbooking.service.controller;

import com.cleanersbooking.service.dto.AvailableCleanerDto;
import com.cleanersbooking.service.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Log4j2
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public List<AvailableCleanerDto> checkCleanersAvailabilityByDate(@RequestParam("date") LocalDate date,
                                                                     @RequestParam(value = "startTime", required = false) LocalTime startTime,
                                                                     @RequestParam(value = "duration", required = false) Integer duration) {
        log.info("Checking availability for date: {}, start time: {}, duration: {}", date, startTime, duration);
        return availabilityService.getAvailableCleanerDtoList(date, startTime, duration);
    }
}
