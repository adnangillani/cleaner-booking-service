package com.cleanersbooking.service.controller;

import com.cleanersbooking.service.dto.BookingDto;
import com.cleanersbooking.service.service.BookingService;
import com.cleanersbooking.service.validation.groups.CreateBookingValidationGroup;
import com.cleanersbooking.service.validation.groups.UpdateBookingValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@Log4j2
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Validated(CreateBookingValidationGroup.class) @RequestBody BookingDto bookingDto) {
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }


    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId,
                                                    @Validated(UpdateBookingValidationGroup.class) @RequestBody BookingDto bookingDto) {
        BookingDto updatedBooking = bookingService.updateBooking(bookingId, bookingDto);
        return ResponseEntity.ok(updatedBooking);
    }
}
