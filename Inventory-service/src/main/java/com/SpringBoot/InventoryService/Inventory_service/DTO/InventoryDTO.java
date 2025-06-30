package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;

public class InventoryDTO {
    private Long inventoryId;
    private Long hotelId;
    private RoomType roomType;
    private Integer totalRooms;
    private Integer availableRooms;


    public InventoryDTO(Long inventoryId, Long hotelId, RoomType roomType, Integer totalRooms, Integer availableRooms) {
        this.inventoryId = inventoryId;
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
    }

    public InventoryDTO() {
    }

    public static InventoryDTOBuilder builder() {
        return new InventoryDTOBuilder();
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

    public static class InventoryDTOBuilder {
        private Long inventoryId;
        private Long hotelId;
        private RoomType roomType;
        private Integer totalRooms;
        private Integer availableRooms;

        InventoryDTOBuilder() {
        }

        public InventoryDTOBuilder inventoryId(Long inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public InventoryDTOBuilder hotelId(Long hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        public InventoryDTOBuilder roomType(RoomType roomType) {
            this.roomType = roomType;
            return this;
        }

        public InventoryDTOBuilder totalRooms(Integer totalRooms) {
            this.totalRooms = totalRooms;
            return this;
        }

        public InventoryDTOBuilder availableRooms(Integer availableRooms) {
            this.availableRooms = availableRooms;
            return this;
        }

        public InventoryDTO build() {
            return new InventoryDTO(this.inventoryId, this.hotelId, this.roomType, this.totalRooms, this.availableRooms);
        }

        public String toString() {
            return "InventoryDTO.InventoryDTOBuilder(inventoryId=" + this.inventoryId + ", hotelId=" + this.hotelId + ", roomType=" + this.roomType + ", totalRooms=" + this.totalRooms + ", availableRooms=" + this.availableRooms + ")";
        }
    }
}

