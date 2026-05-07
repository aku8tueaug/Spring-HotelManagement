package com.SpringBoot.HotelService.Hotel_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "hotels",
        indexes = {
                @Index(name = "idx_hotel_name", columnList = "name"),
                @Index(name = "idx_hotel_city", columnList = "hotel_city")
        },
        uniqueConstraints = { @UniqueConstraint(name = "uk_name_city", columnNames = {"name","hotel_city"})}
        )
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Embedded
    private Address address;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1–5

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "image_url")
    private String imageUrl; // S3/CDN URL

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
