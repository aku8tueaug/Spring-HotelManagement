package com.SpringBoot.RoomService.Room_service.Service;

import com.SpringBoot.RoomService.Room_service.DTO.CreateMultipleRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.CreateRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.ResponseRoomDTO;
import com.SpringBoot.RoomService.Room_service.DTO.UpdateRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
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
    ResponseRoomDTO updateRoom(Long roomId, UpdateRoomRequestDTO request);


}
