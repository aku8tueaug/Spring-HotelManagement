package com.SpringBoot.RoomService.Room_service.ServiceImpl;

import com.SpringBoot.RoomService.Room_service.DTO.*;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.Exception.ResourceNotFoundException;
import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import com.SpringBoot.RoomService.Room_service.HTTPClient.InventoryClient;
import com.SpringBoot.RoomService.Room_service.Repository.RoomRepository;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    @Value("${inventory.defaultHorizonDays}")
    private Integer defaultHorizonDays;

    private final RoomRepository roomRepository;
    private final HotelClient hotelClient;
    private final InventoryClient inventoryClient;


    @Override
    public ResponseRoomDTO addRoom(CreateRoomRequestDTO roomRequestDTO) {
        hotelClient.validateHotelExists(roomRequestDTO.hotelId());

        Room room = Room.builder()
                .hotelId(roomRequestDTO.hotelId())
                .roomNumber(roomRequestDTO.roomNumber())
                .roomType(roomRequestDTO.roomType())
                .status(roomRequestDTO.roomStatus())
                .build();

        Room savedRoom = roomRepository.save(room);

        //Increase Inventory
        if(savedRoom.getStatus() == RoomStatus.ACTIVE) {
            inventoryClient.increaseInventory(
                    entityToInventoryAdjustmentRequestDTO(savedRoom)
            );
        }

        return  entityToResponseRoomDTO(savedRoom);
    }

    @Override
    public List<ResponseRoomDTO> addMultipleRoom(CreateMultipleRoomRequestDTO multipleRoomRequestDTO) {
        hotelClient.validateHotelExists(multipleRoomRequestDTO.hotelId());
        List<Room> rooms = new ArrayList<>();
        for(int i=0;i< multipleRoomRequestDTO.roomCount();i++)
        {
            int generatedRoomNumber = multipleRoomRequestDTO.startRoomNumber()+i;

            Room room = Room.builder()
                    .hotelId(multipleRoomRequestDTO.hotelId())
                    .roomNumber(String.valueOf(generatedRoomNumber))
                    .roomType(multipleRoomRequestDTO.roomType())
                    .status(multipleRoomRequestDTO.roomStatus())
                    .build();
            rooms.add(room);
        }
        List<Room> savedRooms = roomRepository.saveAll(rooms);

        //Increase Inventory
        if(multipleRoomRequestDTO.roomStatus() == RoomStatus.ACTIVE) {
            inventoryClient.increaseInventory(
                    entityToInventoryAdjustmentRequestDTO(multipleRoomRequestDTO)
            );
        }

        return entityToResponseRoomDTO(savedRooms);

    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseRoomDTO> getActiveRooms() {
        return entityToResponseRoomDTO( roomRepository.findByStatus(RoomStatus.ACTIVE));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseRoomDTO> getRoomByRoomType(RoomType roomType) {
        return entityToResponseRoomDTO(  roomRepository.findByRoomType(roomType));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseRoomDTO> getRoomByHotelId(Long hotelId) {
        hotelClient.validateHotelExists(hotelId);
        return entityToResponseRoomDTO(roomRepository.findByHotelId(hotelId));   // return a list of room List<Room>
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseRoomDTO getRoomByRoomNumber(String roomNumber) {
        return entityToResponseRoomDTO( roomRepository.findByRoomNumber(roomNumber).orElseThrow(
                ()->new NoResourceFoundException("Room not found with number: " + roomNumber)
        ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseRoomDTO> getAllRooms() {
        return entityToResponseRoomDTO( roomRepository.findAll());
    }

    @Override
    public void deactivateRoomsByHotelId(Long hotelId) {

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        for (Room room : rooms) {
            room.setStatus(RoomStatus.INACTIVE);
        }
        roomRepository.saveAll(rooms);
    }

    @Override
    public ResponseRoomDTO updateRoom( Long roomId,UpdateRoomRequestDTO request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + roomId
                        )
                );

        if (request.roomType() != null) {
            room.setRoomType(request.roomType());
        }

        if (request.roomStatus() != null) {
            room.setStatus(request.roomStatus());
        }

        Room updatedRoom = roomRepository.save(room);

        return entityToResponseRoomDTO(updatedRoom);
    }


    //Helper Functions

    private ResponseRoomDTO entityToResponseRoomDTO
            (Room room)
    {
        return  new ResponseRoomDTO(
                room.getRoomId(),
                room.getHotelId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getStatus()
        );
    }

    private List<ResponseRoomDTO> entityToResponseRoomDTO
            (List<Room> rooms)
    {
        List<ResponseRoomDTO> responseRoomDTOList = new ArrayList<>();
        for(Room room : rooms)
        {
            responseRoomDTOList.add(entityToResponseRoomDTO(room));
        }

        return responseRoomDTOList;
    }

    private InventoryAdjustmentRequestDTO entityToInventoryAdjustmentRequestDTO
            (Room room)
    {

            return new InventoryAdjustmentRequestDTO(
                    room.getHotelId(),
                    room.getRoomType(),
                    1,
                    defaultHorizonDays
            );
    }

    private InventoryAdjustmentRequestDTO entityToInventoryAdjustmentRequestDTO
            (CreateMultipleRoomRequestDTO roomRequestDTO)
    {

        return new InventoryAdjustmentRequestDTO(
                roomRequestDTO.hotelId(),
                roomRequestDTO.roomType(),
                roomRequestDTO.roomCount(),
                defaultHorizonDays
        );
    }


}
