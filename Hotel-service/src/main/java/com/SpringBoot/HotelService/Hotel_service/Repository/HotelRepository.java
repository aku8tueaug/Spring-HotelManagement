package com.SpringBoot.HotelService.Hotel_service.Repository;

import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
}
