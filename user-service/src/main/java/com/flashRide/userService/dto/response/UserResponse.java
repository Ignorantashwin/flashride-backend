package com.flashRide.userService.dto.response;

import com.flashRide.userService.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record UserResponse(Long id, String userId, String userName, Role role, LocalDateTime createdAt) {
}
