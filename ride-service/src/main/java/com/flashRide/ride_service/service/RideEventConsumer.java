package com.flashRide.ride_service.service;


import com.flashRide.ride_service.event.RideMatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {
    private final RideService rideService;

    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 1000))

      @KafkaListener(topics = "ride.matched", groupId = "ride-service-group")
    public void consumeMatchedRideEvent(RideMatchedEvent event) {
        rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
    }
    @DltHandler
    public void handleDlt(RideMatchedEvent event, Exception ex){
        log.error("matched request permanently failed and moved to dlt : {}", event.getRideId(), ex);
    }
}