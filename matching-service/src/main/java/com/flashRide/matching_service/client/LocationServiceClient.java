package com.flashRide.matching_service.client;

import com.flashRide.locationService.dto.NearByDriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Component
@FeignClient(name= "location-service", url = "${location.service.url}")
public interface LocationServiceClient {
    @GetMapping("/v1/location/driver/nearby")
    List<NearByDriverResponse> nearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    );

}
