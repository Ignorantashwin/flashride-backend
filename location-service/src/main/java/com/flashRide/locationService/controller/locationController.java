package com.flashRide.locationService.controller;

import com.flashRide.locationService.dto.DriverLocationRequest;
import com.flashRide.locationService.dto.NearByDriverResponse;
import com.flashRide.locationService.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/location")
@Slf4j
@RequiredArgsConstructor

public class locationController {
    private final LocationService locationService;

    // driver latest location updated each 3 sec
    @PostMapping("/driver/location")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest request){
        locationService.updateDriverLocation(request);
        return ResponseEntity.ok("driver location Updated");
    }
// matching service calls this when ride is requested
    @GetMapping("/driver/nearby")
    public ResponseEntity<List<NearByDriverResponse>>nearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam (defaultValue = "5.0") double radius
    ){
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }

    @DeleteMapping("/driver/{driverId}")
   public ResponseEntity< String> deleteDriver(@PathVariable String driverId){
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("successfully removed offline driver");
   }
}
