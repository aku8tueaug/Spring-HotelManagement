package com.SpringBoot.RoomService.Room_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomId;

    @NotNull(message = "Hotel ID is required")
    Long hotelId;

    @NotBlank(message = "Room Number is required")
    String room_number;

    @NotNull(message = "Room Type must be specified")
    @Enumerated(EnumType.STRING)
    RoomType roomType;


    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    Double basePrice;

    @NotNull(message = "Availability status is required")
    boolean isAvailable;

    public Room() {
    }

    public Room(Long roomId, Long hotelId, String room_number, RoomType roomType, Double basePrice, boolean isAvailable) {
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.room_number = room_number;
        this.roomType = roomType;
        this.basePrice = basePrice;
        this.isAvailable = isAvailable;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
