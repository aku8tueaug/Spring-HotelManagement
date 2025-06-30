package com.SpringBoot.BookingService.Booking_service.Repository;

import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query("""
      SELECT b FROM Booking b
       WHERE b.roomType = :roomType
         AND b.status = 'CONFIRMED'
         AND b.checkInDate < :to
         AND b.checkOutDate > :from
    """)
    List<Booking> findConflicts(
            @Param("roomType") RoomType roomType,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
