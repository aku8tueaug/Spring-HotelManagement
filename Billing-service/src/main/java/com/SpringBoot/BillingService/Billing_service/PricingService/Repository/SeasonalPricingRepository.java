package com.SpringBoot.BillingService.Billing_service.PricingService.Repository;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.SeasonalPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeasonalPricingRepository extends JpaRepository<SeasonalPricing, Long> {
    List<SeasonalPricing> findByHotelIdAndRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long hotelId, RoomType roomType, LocalDate date1, LocalDate date2);

    List<SeasonalPricing> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
}
