package com.SpringBoot.BookingService.Booking_service.Entity;

import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private Long userId;

    private Integer roomCount;

    @ElementCollection
    @NotNull
    private List<Guest> guests;

    @NotNull(message = "Check-in date is required")
    private LocalDateTime plannedCheckInDateTime;

    @NotNull(message = "Check-out date is required")
    private LocalDateTime plannedCheckOutDateTime;

    @ElementCollection
    private List<String> roomNumber;

    private LocalDateTime actualCheckInDateTime;

    private LocalDateTime actualCheckOutDateTime;

    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Builder.Default
    private LocalDate bookingDate = LocalDate.now();

    @Version
    private Long version;

    public Integer getGuestCount() {
        return guests == null ? 0 : guests.size();
    }
}
