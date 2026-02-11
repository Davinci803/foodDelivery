package com.fooddelivery.users.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
//подумать над названием
@Table(name = "user_profile_history",
        indexes = {@Index(name = "user_profile_history_user_recorded", columnList = "user_id, recorded_at")})

public class UserProfileHistory {

    public static UserProfileHistory from(User user) {
        UserProfileHistory h = new UserProfileHistory();
        h.setUserId(user.getId());
        h.setName(user.getName());
        h.setEmail(user.getEmail());
        h.setRole(user.getRole());
        h.setStatus(user.getStatus());
        h.setRecordedAt(Instant.now());
        return h;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

}
