package com.flashRide.ride_service.exceptions;

public class CancelRideException extends RuntimeException{
    public CancelRideException(String message){
        super(message);
    }
}
