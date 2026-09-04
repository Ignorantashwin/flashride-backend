package com.flashRide.userService.service;

import com.flashRide.userService.dto.request.LoginRequest;
import com.flashRide.userService.dto.request.RegisterRequest;
import com.flashRide.userService.dto.response.LoginResponse;
import com.flashRide.userService.dto.response.RegisterResponse;
import com.flashRide.userService.entity.User;
import com.flashRide.userService.enums.Role;
import com.flashRide.userService.repository.UserRepository;
import com.flashRide.userService.security.CustomUserDetails;
import com.flashRide.userService.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse registerUser(RegisterRequest request){
  if (userRepository.existsByUserName(request.userName())){
      throw new RuntimeException("Username already exist");
  }
      String hashPassword = passwordEncoder.encode(request.password());

       User user1 = User.builder()
                .userId(request.userId())
               .userName(request.userName())
               .hashedPassword(hashPassword)
               .createdAt(LocalDateTime.now())
               .role(Role.RIDER)
               .build();

        System.out.println("HASHED PASSWORD = " + user1.getHashedPassword());
User savedUser = userRepository.save(user1);
return new RegisterResponse(savedUser.getId(), savedUser.getUserId(), savedUser.getUserName(), savedUser.getRole(),savedUser.getCreatedAt());

    }

    public LoginResponse loginUser(LoginRequest request){
     Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtService.generateAccessToken(user);
        return LoginResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .jwtToken(token)
                .build();
 }

}
