package com.cleanersbooking.service.service;

import com.cleanersbooking.service.config.AppConfig;
import com.cleanersbooking.service.dto.AvailableCleanerDto;
import com.cleanersbooking.service.dto.TimeSlotDto;
import com.cleanersbooking.service.entity.Booking;
import com.cleanersbooking.service.entity.Cleaner;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.repository.BookingRepository;
import com.cleanersbooking.service.repository.CleanerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AvailabilityService {

    private final CleanerRepository cleanerRepository;
    private final BookingRepository bookingRepository;
    private final AppConfig appConfig;
    private static final Duration BREAK_DURATION = Duration.ofMinutes(30);


    public List<Cleaner> getAvailableCleaners(LocalDate date, LocalTime startTime, Integer duration) {
        // Validate the date (e.g., no bookings on Fridays)
        validateCleanerWorkingDay(date);

        // Validate the requested booking time against working hours (e.g., 08:00-22:00)
        validateCleanerWorkingHours(date, startTime, duration);

        // Fetch all cleaners from the repository
        List<Cleaner> allCleaners = cleanerRepository.findAll();

        // Filter and return available cleaners
        return filterAvailableCleaners(allCleaners, date, startTime, duration);
    }


    public List<AvailableCleanerDto> getAvailableCleanerDtoList(LocalDate date, LocalTime startTime, Integer duration) {
        validateCleanerWorkingDay(date);
        validateCleanerWorkingHours(date, startTime, duration);
        List<Cleaner> cleaners = cleanerRepository.findAll();
        List<AvailableCleanerDto> availableCleanerDtos = new ArrayList<>();
        for (Cleaner cleaner : cleaners) {
            List<Booking> bookings = bookingRepository.findByBookingDateAndCleanerId(date, cleaner.getCleanerId());
            List<TimeSlotDto> cleanerBookingTimeSlots = bookings.stream().map(this::buildTimeSlotDto).toList();
            List<TimeSlotDto> availableSlots = getAvailableSlots(date, bookings);
            if (!availableSlots.isEmpty()) {
                AvailableCleanerDto availableCleanerDto = AvailableCleanerDto.builder()
                        .cleanerId(cleaner.getCleanerId())
                        .cleanerName(cleaner.getCleanerName())
                        .availableSlots(availableSlots)
                        .bookings(cleanerBookingTimeSlots)
                        .build();
                availableCleanerDtos.add(availableCleanerDto);
            }
        }

        if (!Objects.isNull(startTime) && !Objects.isNull(duration)) {
            LocalDateTime bookingStartDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(duration);
            availableCleanerDtos = filterCleanersByAvailability(availableCleanerDtos, bookingStartDateTime, bookingEndDateTime);
        }

        return availableCleanerDtos;
    }

    public List<Cleaner> getAvailableCleanerList(LocalDate date, LocalTime startTime, Integer duration) {
        List<Cleaner> cleaners = cleanerRepository.findAll();
        List<AvailableCleanerDto> availableCleanerDtos = new ArrayList<>();
        for (Cleaner cleaner : cleaners) {
            List<Booking> bookings = bookingRepository.findByBookingDateAndCleanerId(date, cleaner.getCleanerId());
            List<TimeSlotDto> cleanerBookingTimeSlots = bookings.stream().map(this::buildTimeSlotDto).toList();
            List<TimeSlotDto> availableSlots = getAvailableSlots(date, bookings);
            if (!availableSlots.isEmpty()) {
                AvailableCleanerDto availableCleanerDto = AvailableCleanerDto.builder()
                        .cleanerId(cleaner.getCleanerId())
                        .cleanerName(cleaner.getCleanerName())
                        .availableSlots(availableSlots)
                        .bookings(cleanerBookingTimeSlots)
                        .build();
                availableCleanerDtos.add(availableCleanerDto);
            }
        }

        if (!Objects.isNull(startTime) && !Objects.isNull(duration)) {
            LocalDateTime bookingStartDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(duration);
            availableCleanerDtos = filterCleanersByAvailability(availableCleanerDtos, bookingStartDateTime, bookingEndDateTime);
        }

        List<Long> availableCleanerIdList = availableCleanerDtos.stream().map(AvailableCleanerDto::getCleanerId).toList();
        return cleaners.stream().filter(cleaner -> availableCleanerIdList.contains(cleaner.getCleanerId())).toList();
    }

    private List<AvailableCleanerDto> filterCleanersByAvailability(
            List<AvailableCleanerDto> availableCleaners, LocalDateTime bookingStartDateTime, LocalDateTime bookingEndDateTime) {

        return availableCleaners.stream()
                .filter(cleaner -> hasAvailableSlot(cleaner.getAvailableSlots(), bookingStartDateTime, bookingEndDateTime))
                .toList();
    }

    public boolean hasAvailableSlot(List<TimeSlotDto> availableSlots, LocalDateTime bookingStartDateTime, LocalDateTime bookingEndDateTime) {
        // Check if any of the cleaner's available slots fit the requested booking time
        for (TimeSlotDto slot : availableSlots) {
            if (isSlotAvailableForBooking(slot, bookingStartDateTime, bookingEndDateTime)) {
                return true; // At least one slot is available
            }
        }
        return false; // No available slots match the booking time
    }

    private boolean isSlotAvailableForBooking(TimeSlotDto slot, LocalDateTime bookingStartDateTime, LocalDateTime bookingEndDateTime) {
        // Check if the slot overlaps with the desired booking time range
        return (bookingEndDateTime.isBefore(slot.getEndTime()) || bookingEndDateTime.equals(slot.getEndTime())) &&
                (bookingStartDateTime.isAfter(slot.getStartTime()) || bookingStartDateTime.equals(slot.getStartTime()));

    }

    private TimeSlotDto buildTimeSlotDto(Booking booking) {
        LocalDateTime bookingStartDateTime = LocalDateTime.of(booking.getBookingDate(), booking.getBookingStartTime());
        LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(booking.getBookingDuration());
        return TimeSlotDto.builder()
                .startTime(bookingStartDateTime)
                .endTime(bookingEndDateTime)
                .build();
    }

    public List<TimeSlotDto> getAvailableSlots(LocalDate workDay, List<Booking> bookings) {
        List<TimeSlotDto> availableSlots = new ArrayList<>();

        // Define the start and end of the working hours for the day
        LocalDateTime shiftStart = LocalDateTime.of(workDay, appConfig.getWorkingStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(workDay, appConfig.getWorkingEndTime());

        // Sort bookings by start time
        bookings.sort(Comparator.comparing(Booking::getBookingStartTime));

        LocalDateTime currentSlotStart = shiftStart;

        for (Booking booking : bookings) {

            LocalDateTime bookingStart = LocalDateTime.of(workDay, booking.getBookingStartTime());
            LocalDateTime bookingEnd = bookingStart.plusHours(booking.getBookingDuration());

            // Check if there is an available slot before the current booking
            if (currentSlotStart.isBefore(bookingStart.minus(BREAK_DURATION))) {
                availableSlots.add(new TimeSlotDto(currentSlotStart, bookingStart.minus(BREAK_DURATION)));
            }

            // After the booking ends, the next available time is after a 30-minute break
            currentSlotStart = bookingEnd.plus(BREAK_DURATION);
        }

        // Check if there is any free slot after the last booking until the end of the shift
        if (currentSlotStart.isBefore(shiftEnd)) {
            availableSlots.add(new TimeSlotDto(currentSlotStart, shiftEnd));
        }
        return availableSlots;
    }

    public boolean isCleanerAvailableForUpdate(Cleaner cleaner, LocalDate date, LocalTime startTime, Integer duration, Booking currentBooking) {
        List<Booking> bookings = bookingRepository.findByBookingDateAndCleanerId(date, cleaner.getCleanerId());

        if (bookings == null) {
            bookings = new ArrayList<>();
        }

        // Exclude the current booking from the list of bookings
        bookings = bookings.stream()
                .filter(booking -> !booking.getBookingId().equals(currentBooking.getBookingId()))
                .collect(Collectors.toList());

        List<TimeSlotDto> availableSlots = getAvailableSlots(date, bookings);
        if (availableSlots.isEmpty()) {
            return false;
        }
        LocalDateTime bookingStartDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(duration);
        return hasAvailableSlot(availableSlots, bookingStartDateTime, bookingEndDateTime);
    }

    public void validateCleanerWorkingDay(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            throw new ValidationException(2L, "Cleaners are not working on Fridays");
        }
    }

    public void validateCleanerWorkingHours(LocalDate date, LocalTime startTime, Integer duration) {
        if (Objects.isNull(startTime) && Objects.isNull(duration)) {
            return;
        }
        log.info("Validating working hours for date: {}, start time: {}, duration: {} hours", date, startTime, duration);
        LocalTime workingStartTime = appConfig.getWorkingStartTime();
        LocalTime workingEndTime = appConfig.getWorkingEndTime();
        log.info("Working hours are from {} to {}", workingStartTime, workingEndTime);

        // Create LocalDateTime for the start and end times
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = startDateTime.plusHours(duration);

        // Create LocalDateTime for the working start and end times (for the same date)
        LocalDateTime workingStartDateTime = LocalDateTime.of(date, workingStartTime);
        LocalDateTime workingEndDateTime = LocalDateTime.of(date, workingEndTime);
        log.info("Calculated startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        // Validate that the start and end times are within the working hours
        if (startDateTime.isBefore(workingStartDateTime) || endDateTime.isAfter(workingEndDateTime)) {
            log.error("Validation failed: The provided time is outside of working hours.");
            throw new ValidationException(2L, "The provided time is outside of working hours i.e. 08:00 to 22:00");
        }

        // Log successful validation
        log.info("Validation successful: The provided time is within working hours.");
    }

    private List<Cleaner> filterAvailableCleaners(List<Cleaner> cleaners, LocalDate date, LocalTime startTime, Integer duration) {
        LocalDateTime requestedStartDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime requestedEndDateTime = requestedStartDateTime.plusHours(duration);

        // Filter cleaners based on their availability and current bookings
        return cleaners.stream()
                .filter(cleaner -> isCleanerAvailable(cleaner, requestedStartDateTime, requestedEndDateTime))
                .collect(Collectors.toList());
    }

    // Method to check if a cleaner is available
    private boolean isCleanerAvailable(Cleaner cleaner, LocalDateTime requestedStartDateTime, LocalDateTime requestedEndDateTime) {
        // Check if the cleaner's bookings are null or empty, and if so, assume they are available
        if (cleaner.getBookings() == null || cleaner.getBookings().isEmpty()) {
            // No bookings, cleaner is available
            return true;
        }

        // Check cleaner's bookings to ensure there is no overlap with the requested time
        return cleaner.getBookings().stream().allMatch(booking -> {
            LocalDateTime bookingStartDateTime = LocalDateTime.of(booking.getBookingDate(), booking.getBookingStartTime());
            LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(booking.getBookingDuration());

            // Ensure the requested time doesn't overlap with the booking (considering the 30-minute break)
            return bookingEndDateTime.plusMinutes(30).isBefore(requestedStartDateTime) || bookingStartDateTime.isAfter(requestedEndDateTime.plusMinutes(30));
        });
    }
}

