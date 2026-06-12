package com.SpringBoot.BookingService.Booking_service.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Guest {
    private String fullName;
    private String idType;
    private String idNumber;
    private Integer age;
}
