package com.SpringBoot.BookingService.Booking_service.Repository;

import com.SpringBoot.BookingService.Booking_service.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person,Long> {
    Optional<Person> findByNameAndDOBAndPincodeAndAddressAndMobileNumber(String name, LocalDate DOB, String pincode, String address, String mobileNumber);
}
