package com.flashRide.ride_service.controller;

import com.flashRide.ride_service.dto.RideRequest;
import com.flashRide.ride_service.dto.RideResponse;
import com.flashRide.ride_service.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/rides")
@Slf4j
@RequiredArgsConstructor
public class rideController {
    private final RideService rideService;

    // rider requests or bookRide

    @PostMapping("/request")
    public ResponseEntity<RideResponse>bookRide(@Valid @RequestBody RideRequest request){
        log.info("Ride request received with id : {}", request.riderId());
         RideResponse response = rideService.requestRide(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId){
        log.info("Ride details with id : {}", rideId);
       return ResponseEntity.ok(rideService.rideDetailsById(rideId));
    }

    @GetMapping("rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRidesByRider(@PathVariable String riderId){
        return ResponseEntity.ok(rideService.allRidesByRider(riderId));
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PutMapping("/complete/{rideId}")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @PutMapping("/cancel/{rideId}")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable String rideId){
        log.info("Ride cancelled with id : {}", rideId);
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }
}
