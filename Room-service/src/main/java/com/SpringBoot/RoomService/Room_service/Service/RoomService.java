package com.SpringBoot.RoomService.Room_service.Service;

import com.SpringBoot.RoomService.Room_service.DTO.*;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

import java.util.List;


public interface RoomService {

    public ResponseRoomDTO addRoom(CreateRoomRequestDTO createRoomRequestDTO);
    public List<ResponseRoomDTO> addMultipleRoom(CreateMultipleRoomRequestDTO multipleRoomRequestDTO);
    public List<ResponseRoomDTO> getActiveRooms();
    public List<ResponseRoomDTO> getRoomByRoomType(RoomType roomType);
    public List<ResponseRoomDTO> getRoomByHotelId(Long hotelId);
    public ResponseRoomDTO getRoomByRoomNumber(String roomNumber);
    public List<ResponseRoomDTO> getAllRooms();
    public void deactivateRoomsByHotelId(Long hotelId);
    public void reactivateRoomsByHotelId(Long hotelId);
    ResponseRoomDTO updateRoom(Long roomId, UpdateRoomRequestDTO request);
    public List<RoomSummaryDTO> getRoomByHotelIdAndRoomType(Long hotelId, RoomType roomType);


}
