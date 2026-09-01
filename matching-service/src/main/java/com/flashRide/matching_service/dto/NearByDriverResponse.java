package com.flashRide.matching_service.dto;

public record NearByDriverResponse(String driverId, double latitude, double longitude, double distanceInKm){
}
