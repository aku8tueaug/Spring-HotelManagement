package com.SpringBoot.RoomService.Room_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "room",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_number"})
        }
)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomId;

    @NotNull(message = "Hotel ID is required")
    Long hotelId;

    @NotBlank(message = "Room Number is required")
    String roomNumber;

    @NotNull(message = "Room Type must be specified")
    @Enumerated(EnumType.STRING)
    RoomType roomType;

    @NotNull(message = "Availability status is required")
    boolean isAvailable;

}
