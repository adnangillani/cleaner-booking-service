package com.cleanersbooking.service.repository;

import com.cleanersbooking.service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b inner join b.cleaners cleaners where b.bookingDate = ?1 and cleaners.cleanerId = ?2")
    List<Booking> findByBookingDateAndCleanerId(LocalDate bookingDate, Long cleanerId);

}
