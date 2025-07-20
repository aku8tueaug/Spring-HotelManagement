package com.SpringBoot.BillingService.Billing_service.PricingService.Repository;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Repository
public interface RoomPricingRepository extends JpaRepository<RoomPricing, Long> {
    Optional<RoomPricing> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
}
