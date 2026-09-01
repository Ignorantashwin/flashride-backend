package com.flashRide.ride_service.dto;

import com.flashRide.ride_service.enums.RideStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RideResponse(String id , String driverId, String riderId, String driverName, double pickupLatitude, double pickupLongitude,
                           String pickupAddress, String dropAddress, double dropLatitude, double dropLongitude, double estimatedFare, double actualFare,
                            double distanceInKm,   RideStatus status,
                           LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime startedAt, LocalDateTime completedAt, LocalDateTime cancelledAt) {

}
