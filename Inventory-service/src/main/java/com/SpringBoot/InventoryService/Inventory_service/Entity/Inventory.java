package com.SpringBoot.InventoryService.Inventory_service.Entity;


import jakarta.persistence.*;

@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    private Long hotelId;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    private Integer totalRooms;

    @Column(nullable = false)
    private Integer availableRooms;


    public Inventory(Long inventoryId, Long hotelId, RoomType roomType, Integer totalRooms, Integer availableRooms) {
        this.inventoryId = inventoryId;
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
    }

    public Inventory() {
    }

    public static InventoryBuilder builder() {
        return new InventoryBuilder();
    }

    public Long getInventoryId() {
        return this.inventoryId;
    }

    public Long getHotelId() {
        return this.hotelId;
    }

    public RoomType getRoomType() {
        return this.roomType;
    }

    public Integer getTotalRooms() {
        return this.totalRooms;
    }

    public Integer getAvailableRooms() {
        return this.availableRooms;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public static class InventoryBuilder {
        private Long inventoryId;
        private Long hotelId;
        private RoomType roomType;
        private Integer totalRooms;
        private Integer availableRooms;

        InventoryBuilder() {
        }

        public InventoryBuilder inventoryId(Long inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public InventoryBuilder hotelId(Long hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        public InventoryBuilder roomType(RoomType roomType) {
            this.roomType = roomType;
            return this;
        }

        public InventoryBuilder totalRooms(Integer totalRooms) {
            this.totalRooms = totalRooms;
            return this;
        }

        public InventoryBuilder availableRooms(Integer availableRooms) {
            this.availableRooms = availableRooms;
            return this;
        }

        public Inventory build() {
            return new Inventory(this.inventoryId, this.hotelId, this.roomType, this.totalRooms, this.availableRooms);
        }

        public String toString() {
            return "Inventory.InventoryBuilder(inventoryId=" + this.inventoryId + ", hotelId=" + this.hotelId + ", roomType=" + this.roomType + ", totalRooms=" + this.totalRooms + ", availableRooms=" + this.availableRooms + ")";
        }
    }
}

