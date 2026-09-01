package com.flashRide.userService.dto.response;

import com.flashRide.userService.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(Long id, String userId, String userName, Role role, LocalDateTime createdAt) {
}
