package com.SpringBoot.InventoryService.Inventory_service.Repository;

import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
    List<Inventory> findByHotelId(Long hotelId);
    List<Inventory> findByRoomType(RoomType roomType);

    Optional<Inventory> findByHotelIdAndRoomTypeAndInventoryDate( Long hotelId,
                                                                  RoomType roomType,
                                                                  LocalDate inventoryDate );

    List<Inventory> findByHotelIdAndRoomTypeAndInventoryDateBetween( Long hotelId,
                                                                  RoomType roomType,
                                                                  LocalDate startDate,
                                                                  LocalDate endDate );
}
