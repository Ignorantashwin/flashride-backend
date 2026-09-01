package com.flashRide.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {


private String rideId;
private String riderId;

// pickup

private double pickupLatitude;
private double pickupLongitude;
private String pickupLocation;

// drop

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;

}
