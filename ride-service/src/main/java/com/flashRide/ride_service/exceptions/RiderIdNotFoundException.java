package com.flashRide.ride_service.exceptions;

public class RiderIdNotFoundException extends RuntimeException{
    public RiderIdNotFoundException(String message){
        super(message);
    }
}
