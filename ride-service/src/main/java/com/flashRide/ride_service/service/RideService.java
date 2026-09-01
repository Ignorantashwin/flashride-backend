package com.flashRide.ride_service.service;
import com.flashRide.ride_service.dto.RideRequest;
import com.flashRide.ride_service.dto.RideResponse;
import com.flashRide.ride_service.entity.Ride;
import com.flashRide.ride_service.enums.RideStatus;
import com.flashRide.ride_service.event.RideRequestedEvent;
import com.flashRide.ride_service.exceptions.CancelRideException;
import com.flashRide.ride_service.exceptions.RideIdNotFoundException;
import com.flashRide.ride_service.exceptions.RiderIdNotFoundException;
import com.flashRide.ride_service.repository.RideRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {
    private final RideRepo rideRepo;

    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

    public RideResponse requestRide(RideRequest request){
        // create ride and store in database

        log.info("Creating ride with rider Id : {}", request.riderId());
        log.info("Pickup Address: {}", request.pickupAddress());
        log.info("Drop Address: {}", request.dropAddress());

    Ride ride = Ride.builder()
        .riderId(request.riderId())
        .pickupLatitude(request.pickupLatitude())
        .pickupLongitude(request.pickupLongitude())
        .pickupAddress(request.pickupAddress())
        .dropLatitude(request.dropLatitude())
        .dropLongitude(request.dropLongitude())
        .dropAddress(request.dropAddress())
            .distanceInKm(calculateDistanceInKm(request.pickupLatitude(), request.pickupLongitude(), request.dropLatitude(), request.dropLongitude()))
            .estimatedFare(calculateEstimateFare(request))
        .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
        .status(RideStatus.REQUESTED)
        .build();

        log.info("Ride before save: {}", ride);
    Ride savedRide = rideRepo.save(ride);

        log.info("Ride saved successfully: {}", savedRide);
    // publish event to kafka
        // matching service will consume it and find nearest driver

        RideRequestedEvent event = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress()
        );

        // publish event using KafkaTemplate.send to Kafka

       kafkaTemplate.send(RIDE_REQUESTED_TOPIC, savedRide.getId(), event);

        log.info("RideRequestEvent published to Kafka with id : {}", savedRide.getId());

        // now the requested Ride is matching so update the status

        savedRide.setStatus(RideStatus.MATCHING);

       return RideResponse.builder()
               .id(savedRide.getId())
               .riderId(savedRide.getRiderId())
               .pickupLatitude(savedRide.getPickupLatitude())
               .pickupLongitude(savedRide.getPickupLongitude())
               .pickupAddress(savedRide.getPickupAddress())
               .dropLatitude(savedRide.getDropLatitude())
               .dropLongitude(savedRide.getDropLongitude())
               .dropAddress(savedRide.getDropAddress())
               .distanceInKm(savedRide.getDistanceInKm())
               .estimatedFare(savedRide.getEstimatedFare())
               .status(savedRide.getStatus())
               .createdAt(savedRide.getCreatedAt())
               .build();

    }

    public void updateRideWithDriver(String rideId, String driverId){
      Ride ride = rideRepo.findById(rideId).orElseThrow(()-> new RideIdNotFoundException("No ride found with Id : " + rideId));
      ride.setDriverId(driverId);
      ride.setStatus(RideStatus.ACCEPTED);
      rideRepo.save(ride);
    }

    // Using simplified Haversine distance calculation

    private double calculateEstimateFare(RideRequest request){
       double lat1 = Math.toRadians(request.pickupLatitude());
       double lat2 = Math.toRadians(request.dropLatitude());

       double log1 = Math.toRadians(request.pickupLongitude());
       double log2 = Math.toRadians(request.dropLongitude());

       double dLat = lat2-lat1;
       double dLog = log2 - log1;

       // calculating intermediate value

        double a = Math.pow(Math.sin(dLat /2),2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLog/2),2);

        // angle
        double c = 2 * Math.asin(Math.sqrt(a));

        double distanceInKm = 6371 * c;

        //  calculating base fare 50rs + 10 per/km

        double fare = 50 + (distanceInKm * 10);
        return Math.round(fare * 100.0)/100.0;

    }

    public RideResponse startRide(String rideId){
        Ride ride = rideRepo.findById(rideId).orElseThrow(()-> new RideIdNotFoundException("No ride found with id : " + rideId));
        log.info("Starting ride with id : {}", rideId);
        if (ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cannot be started. Current Status : " + ride.getStatus());
        }
         ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        ride.setUpdatedAt(LocalDateTime.now());
       // ride.setDriverId(ride.getDriverId());
        rideRepo.save(ride);
        return RideResponse.builder()
                .id(ride.getId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupAddress(ride.getPickupAddress())
                .dropLatitude(ride.getDropLatitude())
                .dropLongitude(ride.getDropLongitude())
                .dropAddress(ride.getDropAddress())
                .distanceInKm(ride.getDistanceInKm())
                .estimatedFare(ride.getEstimatedFare())
                .actualFare(ride.getActualFare() == null ? 0.0 : ride.getActualFare())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .startedAt(ride.getStartedAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }

    public RideResponse completeRide(String rideId){
        Ride ride = rideRepo.findById(rideId).orElseThrow(()-> new RideIdNotFoundException("Ride Not Found with id : " + rideId));
        if (ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cannot be completed . Current Status : " + ride.getStatus());
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare() - 20);
        ride.setUpdatedAt(LocalDateTime.now());
        rideRepo.save(ride);
        return RideResponse.builder()
                .id(ride.getId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupAddress(ride.getPickupAddress())
                .dropLatitude(ride.getDropLatitude())
                .dropLongitude(ride.getDropLongitude())
                .dropAddress(ride.getDropAddress())
                .distanceInKm(ride.getDistanceInKm())
                .estimatedFare(ride.getEstimatedFare())
                .actualFare(ride.getActualFare())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .startedAt(ride.getStartedAt())
                .updatedAt(ride.getUpdatedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }

    public RideResponse cancelRide(String rideId){
        Ride ride = rideRepo.findById(rideId).orElseThrow(()-> new RideIdNotFoundException("Ride not found with id : " + rideId));
        if (ride.getStatus() == RideStatus.RIDE_STARTED || ride.getStatus() == RideStatus.COMPLETED){
           throw new CancelRideException("Ride Cannot be cancelled with current status : " + ride.getStatus());
        }
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setUpdatedAt(LocalDateTime.now());
        rideRepo.save(ride);
        return RideResponse.builder()
                .id(ride.getId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupAddress(ride.getPickupAddress())
                .dropLatitude(ride.getDropLatitude())
                .dropLongitude(ride.getDropLongitude())
                .dropAddress(ride.getDropAddress())
                .distanceInKm(ride.getDistanceInKm())
                .estimatedFare(ride.getEstimatedFare())
                .actualFare(ride.getActualFare() == null ? 0.0 : ride.getActualFare())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .cancelledAt(ride.getCancelledAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }

    public RideResponse rideDetailsById(String rideId){
        Ride ride = rideRepo.findById(rideId).orElseThrow(()-> new RideIdNotFoundException("Ride not Found with id : " + rideId));
        return RideResponse.builder()
                .id(ride.getId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupAddress(ride.getPickupAddress())
                .dropLatitude(ride.getDropLatitude())
                .dropLongitude(ride.getDropLongitude())
                .dropAddress(ride.getDropAddress())
                .distanceInKm(ride.getDistanceInKm())
                .estimatedFare(ride.getEstimatedFare())
                .actualFare(ride.getActualFare() == null ? 0.0 : ride.getActualFare())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .updatedAt(ride.getUpdatedAt())
                .startedAt(ride.getStartedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }

    public List<RideResponse> allRidesByRider(String riderId){
        if (!rideRepo.findRiderByRiderId(riderId)){
            throw new RiderIdNotFoundException("Rider Not Found with Id : " + riderId);
        }
       return rideRepo.findByRiderIdOrderByCreatedAtDesc(riderId).stream().map(rides -> RideResponse.builder()
               .id(rides.getId())
               .riderId(rides.getRiderId())
               .driverId(rides.getDriverId())
               .pickupLatitude(rides.getPickupLatitude())
               .pickupLongitude(rides.getPickupLongitude())
               .pickupAddress(rides.getPickupAddress())
               .dropLatitude(rides.getDropLatitude())
               .dropLongitude(rides.getDropLongitude())
               .dropAddress(rides.getDropAddress())
               .status(rides.getStatus())
               .distanceInKm(rides.getDistanceInKm())
               .estimatedFare(rides.getEstimatedFare())
               .actualFare(rides.getActualFare())
               .createdAt(rides.getCreatedAt())
               .startedAt(rides.getStartedAt())
               .completedAt(rides.getCompletedAt())
               .build()
       ).toList();
    }

    // using simplified haversine formula

    public double calculateDistanceInKm(double pickLatitude, double pickLongitude, double dropLatitude2 , double dropLongitude2){
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(dropLatitude2 - pickLatitude);
        double dLon = Math.toRadians(dropLongitude2 - pickLongitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(pickLatitude))
                * Math.cos(Math.toRadians(dropLatitude2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

         double km = earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
         return Math.round(km * 100.0)/100.0 ;
    }
}
