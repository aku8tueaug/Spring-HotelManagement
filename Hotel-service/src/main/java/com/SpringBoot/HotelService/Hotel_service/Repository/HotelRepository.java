package com.SpringBoot.HotelService.Hotel_service.Repository;

import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {

    Optional<Hotel> findByIdAndActiveTrue(Long id);
    Page<Hotel> findByActiveTrue(Pageable pageable);

}
