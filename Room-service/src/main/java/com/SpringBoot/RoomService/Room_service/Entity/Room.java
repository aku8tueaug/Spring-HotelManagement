package com.SpringBoot.RoomService.Room_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "room",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hotel_id","room_number"})
        }
)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomId;

    @Column(name = "hotel_id", nullable = false)
    Long hotelId;

    @Column(name = "room_number", nullable = false)
    String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RoomStatus status;

}
