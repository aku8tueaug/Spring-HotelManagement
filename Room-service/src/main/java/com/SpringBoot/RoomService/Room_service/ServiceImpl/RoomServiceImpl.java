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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        } else if (savedRoom.getStatus().isBlocked()) {
            inventoryClient.increaseInventory(
                    entityToInventoryAdjustmentRequestDTO(savedRoom)
            );
            //Need to block the Inventory
            inventoryClient.blockInventory(
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

        //Increase Inventory
        if(multipleRoomRequestDTO.roomStatus() == RoomStatus.ACTIVE) {
            inventoryClient.increaseInventory(
                    entityToInventoryAdjustmentRequestDTO(multipleRoomRequestDTO)
            );
        } else if(multipleRoomRequestDTO.roomStatus().isBlocked())
        {
            inventoryClient.increaseInventory(
                    entityToInventoryAdjustmentRequestDTO(multipleRoomRequestDTO)
            );
            inventoryClient.blockInventory(
                    entityToInventoryAdjustmentRequestDTO(multipleRoomRequestDTO)
            );
        }
        List<Room> savedRooms = roomRepository.saveAll(rooms);
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
        Map<RoomType, Integer> activeCounts = new HashMap<>();
        Map<RoomType, Integer> blockedCounts = new HashMap<>();

        for (Room room : rooms) {
            if (room.getStatus() == RoomStatus.INACTIVE) {
                continue;
            }
            if (room.getStatus() == RoomStatus.ACTIVE) {
                activeCounts.put(room.getRoomType(), activeCounts.getOrDefault(room.getRoomType(), 0) + 1);
            } else if (room.getStatus().isBlocked()) {
                blockedCounts.put(room.getRoomType(), blockedCounts.getOrDefault(room.getRoomType(), 0) + 1);
            }
            room.setStatus(RoomStatus.INACTIVE);
        }

        activeCounts.forEach((roomType, count) -> {
            if (count > 0) {
                inventoryClient.decreaseInventory(
                        new InventoryAdjustmentRequestDTO(hotelId, roomType, count, defaultHorizonDays)
                );
            }
        });

        blockedCounts.forEach((roomType, count) -> {
            if (count > 0) {
                inventoryClient.unblockInventory(
                        new InventoryAdjustmentRequestDTO(hotelId, roomType, count, defaultHorizonDays)
                );
                inventoryClient.decreaseInventory(
                        new InventoryAdjustmentRequestDTO(hotelId, roomType, count, defaultHorizonDays)
                );
            }
        });

        roomRepository.saveAll(rooms);
    }

    @Override
    public void reactivateRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        Map<RoomType, Integer> inactiveCounts = new HashMap<>();

        for (Room room : rooms) {
            if (room.getStatus() == RoomStatus.ACTIVE) {
                continue;
            }
            if (room.getStatus() == RoomStatus.INACTIVE) {
                inactiveCounts.put(room.getRoomType(), inactiveCounts.getOrDefault(room.getRoomType(), 0) + 1);
            }
            room.setStatus(RoomStatus.ACTIVE);
        }

        inactiveCounts.forEach((roomType, count) -> {
            if (count > 0) {
                inventoryClient.increaseInventory(
                        new InventoryAdjustmentRequestDTO(hotelId, roomType, count, defaultHorizonDays)
                );
            }
        });

        roomRepository.saveAll(rooms);
    }

    @Override
    public ResponseRoomDTO updateRoom( Long roomId,UpdateRoomRequestDTO request) {

        if(request.roomStatus() !=null
            && request.roomStatus() == RoomStatus.INACTIVE)
        {
             throw new IllegalArgumentException(
                    "Room cannot be manually set to INACTIVE"
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + roomId
                        )
                );

        if (request.roomType() != null &&
            room.getRoomType() != request.roomType()) {

            RoomType oldType = room.getRoomType();
            RoomType newType = request.roomType();

            if(room.getStatus() == RoomStatus.ACTIVE)
            {
                //Decreasing the Previous type total number of room
                inventoryClient.decreaseInventory(
                        entityToInventoryAdjustmentRequestDTO(room,oldType)
                );

                //Increasing the New Type total number of room
                inventoryClient.increaseInventory(
                        entityToInventoryAdjustmentRequestDTO(room,newType)
                );
            } else if (room.getStatus().isBlocked()) {
                //Unblock the old type first
                log.info("UNBLOCK OLD {}", oldType);
                inventoryClient.unblockInventory(
                        entityToInventoryAdjustmentRequestDTO(room,oldType)
                );
                log.info("Unblock completed");
                //Decrease the old type
                log.info("DECREASE OLD {}", oldType);
                inventoryClient.decreaseInventory(
                        entityToInventoryAdjustmentRequestDTO(room,oldType)
                );
                log.info("Decrease completed");
                //Increase the new Type
                log.info("INCREASE NEW {}", newType);
                inventoryClient.increaseInventory(
                        entityToInventoryAdjustmentRequestDTO(room,newType)
                );
                log.info("Increase completed");
                //Block the new type
                log.info("BLOCK NEW {}", newType);
                inventoryClient.blockInventory(
                        entityToInventoryAdjustmentRequestDTO(room,newType)
                );
                log.info("Block completed");

            }
            room.setRoomType(request.roomType());

        }


        if (request.roomStatus() != null) {
            RoomStatus oldStatus = room.getStatus();
            RoomStatus newStatus = request.roomStatus();

            //Active to Blocked
            if(oldStatus == RoomStatus.ACTIVE
                && newStatus.isBlocked())
            {
                inventoryClient.blockInventory(
                        entityToInventoryAdjustmentRequestDTO(room)
                );
            }
            //Blocked to Unblocked
            else if(oldStatus.isBlocked()
                    && newStatus == RoomStatus.ACTIVE)
            {
                inventoryClient.unblockInventory(
                        entityToInventoryAdjustmentRequestDTO(room));
            }

            room.setStatus(request.roomStatus());

        }

        Room updatedRoom = roomRepository.save(room);

        return entityToResponseRoomDTO(updatedRoom);
    }

    @Override
    public List<RoomSummaryDTO> getRoomByHotelIdAndRoomType(Long hotelId, RoomType roomType) {
        hotelClient.validateHotelExists(hotelId);

        List<Room> rooms =  roomRepository.findByHotelIdAndRoomTypeAndStatus(hotelId,roomType, RoomStatus.ACTIVE);
        List<RoomSummaryDTO> responseDTOList = new ArrayList<>();
        for(Room room : rooms)
        {
            responseDTOList.add(
                    new RoomSummaryDTO(
                            room.getHotelId(),
                            room.getRoomType(),
                            room.getRoomNumber()
                    )
            );
        }
        return responseDTOList;
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

    private InventoryAdjustmentRequestDTO entityToInventoryAdjustmentRequestDTO
            (Room room, RoomType roomType)
    {

        return new InventoryAdjustmentRequestDTO(
                room.getHotelId(),
                roomType,
                1,
                defaultHorizonDays
        );
    }


}
