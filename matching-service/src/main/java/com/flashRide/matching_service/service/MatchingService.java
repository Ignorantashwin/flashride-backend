package com.flashRide.matching_service.service;

import com.flashRide.matching_service.client.LocationServiceClient;
import com.flashRide.matching_service.event.RideMatchedEvent;
import com.flashRide.ride_service.event.RideRequestedEvent;
import com.flashRide.locationService.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double RADIUS_DEFAULT_SEARCH_KM = 5.0;

    /**
     * main matching Algorithm
     * called when rideRequestedEvent consumed from kafka
     * @param event
     *
     * steps /

     * 1. call method nearByDrivers from LocationService using client
     * 2. find the best Driver from the list of drivers found
     * 3. publish RideMatchedEvent to Kafka
     *
     */

    public void matchDriverForRide(RideRequestedEvent event){
       List<NearByDriverResponse> nearByDrivers =  locationServiceClient.nearByDrivers(event.getPickupLatitude(), event.getPickupLongitude(),RADIUS_DEFAULT_SEARCH_KM);
       if (nearByDrivers.isEmpty()){
           log.warn("No drivers found for the ride");
           return;
       }
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDrivers);
       if (bestDriver.isEmpty()){
           log.warn("Could not find suitable driver for ride");
           return;
       }
       NearByDriverResponse assignedDriver = bestDriver.get();

       RideMatchedEvent matchedEvent = new RideMatchedEvent
               (event.getRiderId(), event.getRideId(), assignedDriver.driverId(), assignedDriver.latitude(), assignedDriver.longitude(), assignedDriver.distanceInKm());

       kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), matchedEvent );
       log.info("RideMatchedEvent Published");


    }

    /**
     * finding bestDriver Algorithm
     * driver scoring algorithm
     *
     * Distance = 70%
     * rating = 30%
     *
     * score = (1/distance) * distanceWeight + rating * ratingWeight
     *
     * - why (because the closest driver will get the max score , and the max score = closest driver)
     * ex = d1 = 0.5 km away and d2 = 1km = then dividing by 1 we will get the d1 score = 2 and d2 = 1 ( so this algo fits well)
     *
     * @param nearByDriver
     * @return
     */
    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> nearByDriver){
 double distanceWeight = 0.7;
 double ratingWeight = 0.3;

 return nearByDriver.stream().max(Comparator.comparingDouble(driver -> {

     double distanceScore = 1.0 / driver.distanceInKm();

     // stimulated rating between 4.0 to 5.0
     // in production get rating from driver service

     double stimulatedRating = 4.0 + Math.random();

     // final Weighted Score
    return (distanceScore * distanceWeight) + (stimulatedRating *  ratingWeight);
 }));

    }
}
