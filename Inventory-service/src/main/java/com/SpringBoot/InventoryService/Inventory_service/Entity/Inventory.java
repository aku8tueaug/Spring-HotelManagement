package com.SpringBoot.InventoryService.Inventory_service.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity

@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "hotel_id",
                                "room_type",
                                "inventory_date"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_inventory_hotel_room_date",
                        columnList = "hotel_id, room_type, inventory_date"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Inventory {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long inventoryId;

    @Column(name = "hotel_id", nullable = false)

    private Long hotelId;

    @Enumerated(EnumType.STRING)

    @Column(name = "room_type", nullable = false)

    private RoomType roomType;

    @Column(name = "inventory_date", nullable = false)

    private LocalDate inventoryDate;

    @Column(name = "total_rooms", nullable = false)

    private Integer totalRooms;  //ACTIVE physical rooms, not all room

    @Column(name = "reserved_rooms", nullable = false)

    private Integer reservedRooms;

    @Column(name = "blocked_rooms", nullable = false)

    private Integer blockedRooms; // INACTIVE,MAINTENANCE,RENOVATION,TEMPORARY_BLOCKED, and UNDER_CLEANING rooms

    @Version

    private Long version;

    // Derived field (NOT persisted)

    @Transient

    public Integer getAvailableRooms() {

        return totalRooms - reservedRooms - blockedRooms;

    }

}

