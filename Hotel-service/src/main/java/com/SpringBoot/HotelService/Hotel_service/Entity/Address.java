package com.SpringBoot.HotelService.Hotel_service.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Address {

    @Column(name = "hotel_street", nullable = false, length = 255)
    private String street;

    @Column(name = "hotel_city", nullable = false, length = 100)
    private String city;

    @Column(name = "hotel_state", nullable = false, length = 100)
    private String state;

    @Column(name = "hotel_country", nullable = false, length = 100)
    private String country;

    @Column(name = "hotel_zip_code", nullable = false, length = 10)
    private String zipCode;

}
