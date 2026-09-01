package com.flashRide.userService.service;

import com.flashRide.userService.dto.request.RegisterRequest;
import com.flashRide.userService.dto.response.UserResponse;
import com.flashRide.userService.entity.User;
import com.flashRide.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//
//    public UserResponse registerUser(RegisterRequest request){
//        if (userRepository.findUserByUserName(request.userName())){
//            throw new RuntimeException("UserName already Exist");
//        }

//       User user = User.builder()
//                .userId(request.userId())
//               .userName(request.userName())


   // }
}
