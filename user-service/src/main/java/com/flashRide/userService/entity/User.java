package com.flashRide.userService.entity;

import com.flashRide.userService.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
   private Role role;

    @Column(nullable = false)
   private LocalDateTime createdAt;
}
