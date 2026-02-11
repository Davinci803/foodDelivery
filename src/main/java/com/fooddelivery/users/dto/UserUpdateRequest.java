package com.fooddelivery.users.dto;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private Role role;

    private UserStatus status;

}

