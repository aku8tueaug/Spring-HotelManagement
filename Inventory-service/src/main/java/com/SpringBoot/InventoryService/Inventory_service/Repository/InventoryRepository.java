package com.SpringBoot.InventoryService.Inventory_service.Repository;

import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
    Optional<List<Inventory>> getInventoryByHotelId(Long hotelId);
    Optional<List<Inventory>> getInventoriesByRoomType(RoomType roomType);
}
