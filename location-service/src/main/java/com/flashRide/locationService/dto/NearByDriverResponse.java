package com.flashRide.locationService.dto;

public record NearByDriverResponse(String driverId, double latitude, double longitude, double distanceInKm) {
}
