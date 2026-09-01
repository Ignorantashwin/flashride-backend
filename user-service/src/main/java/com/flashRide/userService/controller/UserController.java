package com.flashRide.userService.controller;

import com.flashRide.userService.dto.request.LoginRequest;
import com.flashRide.userService.dto.request.RegisterRequest;
import com.flashRide.userService.dto.response.UserResponse;
import com.flashRide.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse>createUser(RegisterRequest request){
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<UserResponse> loginUser(LoginRequest request){
        UserResponse response = userService.loginUser(request);
        return ResponseEntity.ok().body(response);
    }
}
