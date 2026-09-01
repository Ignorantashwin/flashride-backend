package com.flashRide.userService.repository;

import com.flashRide.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean findUserByUserName(String userName);

}
