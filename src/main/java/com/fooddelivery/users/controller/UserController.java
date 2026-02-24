package com.fooddelivery.users.controller;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserProfileHistory;
import com.fooddelivery.users.dto.UserCreateRequest;
import com.fooddelivery.users.dto.UserHistoryResponse;
import com.fooddelivery.users.dto.UserResponse;
import com.fooddelivery.users.dto.UserUpdateRequest;
import com.fooddelivery.users.mapper.UserMapper;
import com.fooddelivery.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request) {
        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getByRole(@RequestParam Role role) {
        List<User> users = userService.findByRole(role);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<UserHistoryResponse> getHistoryAt(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime at) {

        Instant instant = at.atZone(ZoneId.systemDefault()).toInstant();
        UserProfileHistory snapshot = userService.getStateAt(id, instant);
        if (snapshot != null) {
            UserHistoryResponse response = userMapper.toHistoryResponse(snapshot);
            return ResponseEntity.ok(response);
        }
        User current = userService.getById(id);
        UserHistoryResponse response = userMapper.toHistoryResponse(current);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history/versions")
    public ResponseEntity<List<UserHistoryResponse>> getHistoryVersions(@PathVariable Long id) {
        userService.getById(id);
        List<UserHistoryResponse> list = userService.getHistory(id).stream()
                .map(userMapper::toHistoryResponse)
                .toList();
        return ResponseEntity.ok(list);
    }
}

