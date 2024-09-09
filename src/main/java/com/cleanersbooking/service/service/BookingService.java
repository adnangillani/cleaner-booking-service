package com.cleanersbooking.service.service;

import com.cleanersbooking.service.dto.BookingDto;
import com.cleanersbooking.service.dto.CleanerDto;
import com.cleanersbooking.service.entity.Booking;
import com.cleanersbooking.service.entity.Cleaner;
import com.cleanersbooking.service.exception.ValidationException;
import com.cleanersbooking.service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookingService {
    private final BookingRepository bookingRepository;
    private final AvailabilityService availabilityService;

    public BookingDto createBooking(BookingDto bookingDto) {
        // Log the input BookingDto
        log.info("Creating booking with details: {}", bookingDto);

        // Validate cleaner working day and hours
        availabilityService.validateCleanerWorkingDay(bookingDto.getBookingDate());
        availabilityService.validateCleanerWorkingHours(bookingDto.getBookingDate(),
                bookingDto.getBookingStartTime(),
                bookingDto.getBookingDuration());

        List<Cleaner> availableCleaners = availabilityService
                .getAvailableCleanerList(bookingDto.getBookingDate(),
                        bookingDto.getBookingStartTime(),
                        bookingDto.getBookingDuration());

        if (availableCleaners == null || availableCleaners.isEmpty()) {
            log.error("No available cleaners found for the given time.");
            throw new ValidationException(2L, "No available cleaners for this time.");
        }

        availableCleaners = getCleanersByVehicleAndCount(availableCleaners, bookingDto.getCleanerCount());

        List<Cleaner> selectedCleaners = availableCleaners.stream()
                .limit(bookingDto.getCleanerCount())
                .toList();

        if (selectedCleaners.size() != bookingDto.getCleanerCount()) {
            throw new ValidationException(2L, "Not enough available cleaners for this time.");
        }

        // Create the booking
        Booking booking = new Booking();
        booking.setBookingDate(bookingDto.getBookingDate());
        booking.setBookingStartTime(bookingDto.getBookingStartTime());
        booking.setBookingDuration(bookingDto.getBookingDuration());
        booking.setCleaners(selectedCleaners);

        // Save the booking
        Booking savedBooking = bookingRepository.save(booking);
        BookingDto resultDto = convertToBookingDto(savedBooking);

        log.info("Returning BookingDto: {}", resultDto);
        return resultDto;
    }


    public BookingDto updateBooking(Long id, BookingDto bookingDto) {

        // Log the input BookingDto
        log.info("Updating booking with details: {}", bookingDto);

        // Validate cleaner working day and hours
        availabilityService.validateCleanerWorkingDay(bookingDto.getBookingDate());
        availabilityService.validateCleanerWorkingHours(bookingDto.getBookingDate(),
                bookingDto.getBookingStartTime(),
                bookingDto.getBookingDuration());

        // Fetch the existing booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ValidationException(2L, "Booking not found"));

        // Set the cleaners currently assigned to this booking
        List<Cleaner> currentCleaners = booking.getCleaners();

        List<Cleaner> selectedCleaners = new ArrayList<>();
        // Check if the current cleaners are available for the new date and time
        for (Cleaner cleaner : currentCleaners) {
            if (availabilityService.isCleanerAvailableForUpdate(cleaner, bookingDto.getBookingDate(), bookingDto.getBookingStartTime(), bookingDto.getBookingDuration(), booking)) {
                selectedCleaners.add(cleaner);
            }
        }

        if (selectedCleaners.size() != currentCleaners.size()) {
            List<Cleaner> availableCleaners = availabilityService
                    .getAvailableCleaners(bookingDto.getBookingDate(), bookingDto.getBookingStartTime(), bookingDto.getBookingDuration());
            availableCleaners = getCleanersByVehicleAndCount(availableCleaners, currentCleaners.size());
            for (Cleaner cleaner : availableCleaners) {
                if (selectedCleaners.size() == currentCleaners.size()) {
                    break;
                }
                if (!selectedCleaners.contains(cleaner.getCleanerId())) {
                    selectedCleaners.add(cleaner);
                }
            }
        }

        if (selectedCleaners.size() != currentCleaners.size()) {
            throw new ValidationException(2L, "Not enough available cleaners for this time.");
        }

        // Update the booking with new details
        booking.setBookingDate(bookingDto.getBookingDate());
        booking.setBookingStartTime(bookingDto.getBookingStartTime());
        booking.setBookingDuration(bookingDto.getBookingDuration());
        booking.setCleaners(selectedCleaners);

        Booking updatedBooking = bookingRepository.save(booking);

        return convertToBookingDto(updatedBooking);
    }


    public List<Cleaner> getCleanersByVehicleAndCount(List<Cleaner> cleaners, int cleanerCount) {
        // Group the cleaners by vehicle_id
        Map<Long, List<Cleaner>> vehicleCleanerMap = cleaners.stream()
                .collect(Collectors.groupingBy(cleaner -> cleaner.getVehicle().getVehicleId()));

        // Log the map with vehicle_id and their associated cleaners
        vehicleCleanerMap.forEach((vehicleId, cleanerList) -> {
            log.info("Vehicle ID: {} has cleaners: {}", vehicleId, cleanerList.stream()
                    .map(Cleaner::getCleanerName)
                    .collect(Collectors.joining(", ")));
        });

        // Find the first vehicle that matches the cleanerCount condition
        Optional<Map.Entry<Long, List<Cleaner>>> matchingVehicleEntry = vehicleCleanerMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= cleanerCount)
                .findFirst();

        // Log and return the cleaners from the first vehicle that matches the condition
        if (matchingVehicleEntry.isPresent()) {
            List<Cleaner> matchingCleaners = matchingVehicleEntry.get().getValue();
            log.info("First vehicle (ID: {}) with {} or more cleaners: {}",
                    matchingVehicleEntry.get().getKey(), cleanerCount,
                    matchingCleaners.stream()
                            .map(Cleaner::getCleanerName)
                            .collect(Collectors.joining(", ")));

            return matchingCleaners;
        } else {
            log.info("No vehicles found with {} or more cleaners.", cleanerCount);
            return Collections.emptyList();  // Return an empty list if no vehicle matches
        }
    }

    public BookingDto convertToBookingDto(Booking booking) {
        // Convert and set the cleaners
        List<CleanerDto> cleanerDtos = booking.getCleaners().stream()
                .map(cleaner -> CleanerDto.builder()
                        .cleanerId(cleaner.getCleanerId())
                        .cleanerName(cleaner.getCleanerName())
                        .build())
                .collect(Collectors.toList());

        // Build the BookingDto using the builder pattern
        return BookingDto.builder()
                .bookingDate(booking.getBookingDate())
                .bookingStartTime(booking.getBookingStartTime())
                .bookingDuration(booking.getBookingDuration())
                .cleanerCount(booking.getCleaners().size())
                .cleaners(cleanerDtos)
                .build();
    }
}
