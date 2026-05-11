package com.SpringBoot.RoomService.Room_service.Service;

import com.SpringBoot.RoomService.Room_service.DTO.CreateMultipleRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.Entity.Room;

import java.util.List;


public interface RoomService {

    public Room addRoom(Room room);
    public List<Room> addMultipleRoom(CreateMultipleRoomRequestDTO multipleRoomRequestDTO);
    public List<Room> getAvailableRooms();
    public List<Room> getRoomByRoomType(String roomType);
    public List<Room> getRoomByHotelId(Long hotelId);
    public Room getRoomByRoomNumber(String roomNumber);
    public List<Room> getAllRooms();


}
