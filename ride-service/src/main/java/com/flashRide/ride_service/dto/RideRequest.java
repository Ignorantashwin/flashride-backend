package com.flashRide.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideRequest(@NotBlank(message = "Rider Id is required") String riderId, @NotNull(message = "Pickup latitude is required") double pickupLatitude,
                         @NotNull(message = "Pickup longitude is required") double pickupLongitude, @NotBlank(message = "Pickup address is required") String pickupAddress,
                         @NotNull(message = "Drop latitude is required") double dropLatitude, @NotNull(message = "Drop longitude is required") double dropLongitude,
                         @NotBlank(message = "Drop address is required") String dropAddress ) {
}
