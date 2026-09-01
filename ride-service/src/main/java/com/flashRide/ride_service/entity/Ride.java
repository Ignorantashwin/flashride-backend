package com.flashRide.ride_service.entity;

import com.flashRide.ride_service.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String driverId;

    @Column(nullable = false)
    private String riderId;

    @Column(nullable = false)
    private Double pickupLatitude;

    @Column(nullable = false)
    private Double pickupLongitude;

    @Column(nullable = false)
    @Size(min = 20, max = 50)
    private String pickupAddress;

    @Column(nullable = false)
    private Double dropLatitude;

    @Column(nullable = false)
    private Double dropLongitude;

    @Column(nullable = false)
    @Size(min = 20, max = 50)
    private String dropAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

   // Fare Details

    private double distanceInKm;

    private Double estimatedFare;

    private Double actualFare;

    // timeStamps

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;




}
