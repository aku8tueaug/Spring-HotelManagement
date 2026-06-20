package com.SpringBoot.BillingService.Billing_service.PricingService.Repository;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RatePlan;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {
    Optional<RatePlan> findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long hotelId, RoomType roomType, LocalDate date1, LocalDate date2);

    List<RatePlan> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
}
