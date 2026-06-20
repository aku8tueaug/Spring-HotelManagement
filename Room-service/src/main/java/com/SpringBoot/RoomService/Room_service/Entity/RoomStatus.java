package com.SpringBoot.RoomService.Room_service.Entity;

public enum RoomStatus {
    ACTIVE,
    INACTIVE,   //Hotel deleted / Room removed
    MAINTENANCE,
    RENOVATION,
    TEMPORARY_BLOCKED,
    UNDER_CLEANING;


    public boolean isBlocked() {
        return this == MAINTENANCE
                || this == RENOVATION
                || this == TEMPORARY_BLOCKED
                || this == UNDER_CLEANING;
    }

}
