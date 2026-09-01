package com.flashRide.matching_service.service;

import com.flashRide.ride_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {
    private final MatchingService matchingService;

    /**
     * flow - ride service -> kafka(ride.requested) -> this consumer -> Matching Service
     * listens to ride.requested kafka topic
     * triggers each time when ride-service publish new ride request
     * using RetryableTopic to avoid retries spam and useful to catch and fix errors
     * if max retries failed the request is sent to the dead letter topic
     *
     * @param event
     */

@RetryableTopic(attempts = "4", backOff = @BackOff(delay = 1000))

    @KafkaListener(topics = "ride.requested", groupId = "matching-service-group")
    public void consumeRideRequestedEvent(RideRequestedEvent event){
    log.info("Processing ride request: {}", event.getRideId());
            matchingService.matchDriverForRide(event);
    }
    @DltHandler
    public void handleDlt(RideRequestedEvent event, Exception ex){
    log.error("Ride request permanently failed and moved to Dlt : {}", event.getRideId(), ex);
    }
}
