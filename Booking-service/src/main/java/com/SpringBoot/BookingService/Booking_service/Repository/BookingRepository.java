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
    @Query("""
      SELECT b FROM Booking b
       WHERE b.hotelId = :hotelId
         AND b.roomType = :roomType
         AND b.roomNumber = :roomNumber
         AND b.status = 'CONFIRMED'
         AND b.checkInDate < :to
         AND b.checkOutDate > :from
    """)
    List<Booking> findConflicts(
            @Param("hotelId") Long hotelId,
            @Param("roomType") RoomType roomType,
            @Param("roomNumber") String roomNumber,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<Booking> findByStatus(BookingStatus status);
    Optional<Booking> findByRoomNumber(String roomNumber);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.hotelId = :hotelId " +
            "AND b.roomType = :roomType " +
            "AND b.status = 'CONFIRMED' " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    List<Booking> findBookingsInDateRange(
            @Param("hotelId") Long hotelId,
            @Param("roomType") RoomType roomType,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

}
