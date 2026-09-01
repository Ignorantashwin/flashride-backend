package com.flashRide.ride_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(LocalDateTime timeStamp, int status, String error, String message, String path) {
}
