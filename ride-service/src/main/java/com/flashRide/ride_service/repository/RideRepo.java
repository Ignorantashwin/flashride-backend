package com.flashRide.ride_service.repository;

import com.flashRide.ride_service.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepo extends JpaRepository<Ride, String> {
    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);
    boolean findRiderByRiderId(String riderId);
}
