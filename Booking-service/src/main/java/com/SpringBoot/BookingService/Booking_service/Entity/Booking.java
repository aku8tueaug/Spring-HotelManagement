package com.SpringBoot.BookingService.Booking_service.Entity;

import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data                  // Generates getters/setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room ID is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @ManyToMany
    @JoinTable(name = "PersonBooking",
            joinColumns = @JoinColumn(name = "bookingId"),
            inverseJoinColumns = @JoinColumn(name = "personId"))
    private List<Person> guests;

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private BigDecimal price;
}
