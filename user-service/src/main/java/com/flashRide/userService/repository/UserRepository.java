package com.flashRide.userService.repository;

import com.flashRide.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
     boolean existsByUserName(String userName);
    Optional<User> findUserByUserName(String userName);

}
