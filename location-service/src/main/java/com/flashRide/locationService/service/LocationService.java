package com.flashRide.locationService.service;

import com.flashRide.locationService.dto.DriverLocationRequest;
import com.flashRide.locationService.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

    // redis key for all driver location
    private static final String DRIVERS_GEO_KEY = "drivers:locations";

   /*
   . Update driver location in redis
   . updating location every 3 sec by driver's phone
   . maps Redis GEOADD commands
    */

    public void updateDriverLocation(DriverLocationRequest request){
        log.info("updating location for driver : {} - lat{} - log{}", request.driverId(), request.latitude(), request.longitude());

        // important * first longitude , then latitude , why - due to GeoSpatial standard

        Point point = new Point(request.longitude(), request.latitude());

        // opsForGeo gave us access to all the GeoSpatial Commands in java

        long result = redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY , point, request.driverId());
        log.info("Redis GEOADD result: {}", result);
    }

    /*
      . find NearBy drivers within given Radius
      . called by matchingService on ride request
      . maps Redis GeoRadius Command
     */

    public List<NearByDriverResponse> findNearByDrivers(double latitude, double longitude, double radiusInKm){
        log.info("finding drivers near lat : {} long : {} withIn : {}", latitude ,longitude, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitude,latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
                DRIVERS_GEO_KEY,
                searchArea,
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeCoordinates()
                        .includeDistance()
                        .sortAscending()
                        .limit(10)
        );
        List<NearByDriverResponse> nearByDrivers = new ArrayList<>();

        if (results != null){
            results.getContent().forEach(result -> {
                        RedisGeoCommands.GeoLocation<String> location = result.getContent();
                        nearByDrivers.add(new NearByDriverResponse(location.getName(), location.getPoint().getY(), location.getPoint().getX(), result.getDistance().getValue()));
                    }
                    );
        }
     log.info("Found {} drivers nearBy ",nearByDrivers.size() );
        return nearByDrivers;
    }

    // remove drivers when they are offline
    // maps to redis ZREM Command

    public void removeDriver(String driverId){
        log.info("removing driver : {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY,driverId);
    }
}
