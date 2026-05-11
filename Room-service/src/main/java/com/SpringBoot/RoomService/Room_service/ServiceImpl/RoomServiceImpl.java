package com.SpringBoot.RoomService.Room_service.ServiceImpl;

import com.SpringBoot.RoomService.Room_service.DTO.CreateMultipleRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.Hotel;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import com.SpringBoot.RoomService.Room_service.Repository.RoomRepository;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelClient hotelClient;


    @Override
    public Room addRoom(Room room) {
        hotelClient.getHotelById(room.getHotelId());
        return roomRepository.save(room);
    }

    @Override
    public List<Room> addMultipleRoom(CreateMultipleRoomRequestDTO multipleRoomRequestDTO) {
        hotelClient.getHotelById(multipleRoomRequestDTO.HotelId());

        RoomType roomType = RoomType.valueOf(multipleRoomRequestDTO.roomType().toUpperCase());
        List<Room> rooms = new ArrayList<>();
        for(int i=0;i< multipleRoomRequestDTO.noOfRoomToBeCreate();i++)
        {
            String roomNumber = multipleRoomRequestDTO.HotelId().toString()
                    + roomType.toString().substring(0,2)
                    + multipleRoomRequestDTO.floorNumber().toString()
                    + String.format("%03d", (multipleRoomRequestDTO.idStart()+i));
            Room room = Room.builder()
                    .hotelId(multipleRoomRequestDTO.HotelId())
                    .roomNumber(roomNumber)
                    .roomType(roomType)
                    .isAvailable(multipleRoomRequestDTO.isAvailable())
                    .build();
            rooms.add(room);
        }
        return roomRepository.saveAll(rooms);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsAvailableTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Room> getRoomByRoomType(String roomType) {
        RoomType roomType1 = RoomType.valueOf(roomType.toUpperCase());
        return roomRepository.findByRoomType(roomType1);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Room> getRoomByHotelId(Long hotelId) {
        hotelClient.getHotelById(hotelId);
        return roomRepository.findByHotelId(hotelId);   // return a list of room List<Room>
    }

    @Transactional(readOnly = true)
    @Override
    public Room getRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
