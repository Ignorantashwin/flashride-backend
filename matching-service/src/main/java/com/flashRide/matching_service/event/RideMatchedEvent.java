package com.flashRide.matching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RideMatchedEvent {
    private String riderId;
    private String rideId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickupKm;
}
