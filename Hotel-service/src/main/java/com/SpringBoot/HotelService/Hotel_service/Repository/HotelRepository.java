package com.SpringBoot.HotelService.Hotel_service.Repository;

import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
}
