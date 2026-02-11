package com.fooddelivery.users.dto;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;

}

