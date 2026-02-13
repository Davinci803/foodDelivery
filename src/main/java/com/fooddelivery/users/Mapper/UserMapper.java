package com.fooddelivery.users.Mapper;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserStatus;
import com.fooddelivery.users.dto.UserHistoryResponse;
import com.fooddelivery.users.dto.UserResponse;

import java.time.Instant;

// todo переделать нормально
public class UserMapper {

    public static UserHistoryResponse toHistoryResponse(Long id, String name, String email, Role role, UserStatus status, Instant recordedAt) {
        UserHistoryResponse r = new UserHistoryResponse();
        r.setId(id);
        r.setName(name);
        r.setEmail(email);
        r.setRole(role);
        r.setStatus(status);
        r.setRecordedAt(recordedAt);
        return r;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        return response;
    }
}
