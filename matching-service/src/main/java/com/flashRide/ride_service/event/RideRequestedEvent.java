package com.flashRide.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RideRequestedEvent {
    private String riderId;
    private String rideId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}
