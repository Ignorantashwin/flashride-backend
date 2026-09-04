package com.flashRide.userService.controller;

import com.flashRide.userService.dto.request.LoginRequest;
import com.flashRide.userService.dto.request.RegisterRequest;
import com.flashRide.userService.dto.response.LoginResponse;
import com.flashRide.userService.dto.response.RegisterResponse;
import com.flashRide.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse>createUser(@RequestBody RegisterRequest request){
        RegisterResponse response = userService.registerUser(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok().body(response);
    }
}
