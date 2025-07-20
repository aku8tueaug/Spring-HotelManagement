package com.SpringBoot.BookingService.Booking_service.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private LocalDate DOB;
    @NotNull
    private String pincode;
    @NotNull
    private String address;

    @ManyToMany(mappedBy = "guests")
    @JsonIgnore
    private List<Booking> bookings;

//    @Transient
//    @NotBlank(message = "Country code is required")
//    @Pattern(regexp = "\\+?[0-9]{1,4}", message = "Invalid country code")
//    private String countryCode = "+91";  // Default value - India

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^\\+?[1-9][0-9]{0,2}[0-9]{6,15}$",
            message = "Invalid mobile number with country code"
    )
    private String mobileNumber;


}
