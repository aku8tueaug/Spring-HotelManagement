package com.SpringBoot.HotelService.Hotel_service.Repository;

import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {

    Optional<Hotel> findByIdAndActiveTrue(Long id);
    Page<Hotel> findByActiveTrue(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Hotel h SET h.active = :active WHERE h.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

}
