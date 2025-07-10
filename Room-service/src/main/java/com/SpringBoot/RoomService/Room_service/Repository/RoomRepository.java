package com.SpringBoot.RoomService.Room_service.Repository;

import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByIsAvailableTrue();
    List<Room> findByRoomType(RoomType roomType);
    Room findByRoomNumber(String roomNumber);
}
