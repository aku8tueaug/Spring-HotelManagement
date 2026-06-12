package com.SpringBoot.BookingService.Booking_service.Repository;

import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByHotelId(Long hotelId);

    @Query("""
        SELECT rn
        FROM Booking b
        JOIN b.roomNumber rn
        WHERE b.hotelId = :hotelId
        AND b.roomType = :roomType
        AND b.status = 'CHECKED_IN'
        """)
    List<String> findOccupiedRooms(
            @Param("hotelId") Long hotelId,
            @Param("roomType") RoomType roomType
    );
}
