package com.fooddelivery.users.controller;

import com.fooddelivery.users.Mapper.UserMapper;
import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserProfileHistory;
import com.fooddelivery.users.domain.UserStatus;
import com.fooddelivery.users.dto.UserCreateRequest;
import com.fooddelivery.users.dto.UserHistoryResponse;
import com.fooddelivery.users.dto.UserResponse;
import com.fooddelivery.users.dto.UserUpdateRequest;
import com.fooddelivery.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static com.fooddelivery.users.Mapper.UserMapper.toHistoryResponse;
import static com.fooddelivery.users.Mapper.UserMapper.toResponse;


// todo так же оптимизировать как в ресторане
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    //Любой
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    //Админ
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    //Админ тольео может банить и менять роль, остально Юзер
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request) {
        User updatedData = new User();
        updatedData.setName(request.getName());
        updatedData.setEmail(request.getEmail());
        updatedData.setRole(request.getRole());
        updatedData.setStatus(request.getStatus());

        User updated = userService.updateUser(id, updatedData);
        return ResponseEntity.ok(toResponse(updated));
    }

    //Админ
    @GetMapping
    public ResponseEntity<List<UserResponse>> getByRole(@RequestParam Role role) {
        List<User> users = userService.findByRole(role);
        List<UserResponse> responses = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    //Админ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    //Админ
    @GetMapping("/{id}/history")
    public ResponseEntity<UserHistoryResponse> getHistoryAt(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime at) {

        Instant instant = at.atZone(ZoneId.systemDefault()).toInstant();
        UserProfileHistory snapshot = userService.getStateAt(id, instant);
        if (snapshot != null) {
            UserHistoryResponse response = toHistoryResponse(snapshot.getUserId(), snapshot.getName(), snapshot.getEmail(),
                    snapshot.getRole(), snapshot.getStatus(), snapshot.getRecordedAt());
            return ResponseEntity.ok(response);
        }
        User current = userService.getById(id);
        UserHistoryResponse response = toHistoryResponse(current.getId(), current.getName(), current.getEmail(),
                current.getRole(), current.getStatus(), null);
        return ResponseEntity.ok(response);
    }

    //Админ
    @GetMapping("/{id}/history/versions")
    public ResponseEntity<List<UserHistoryResponse>> getHistoryVersions(@PathVariable Long id) {
        userService.getById(id);
        List<UserHistoryResponse> list = userService.getHistory(id).stream()
                .map(history -> toHistoryResponse(history.getUserId(), history.getName(), history.getEmail(), history.getRole(), history.getStatus(), history.getRecordedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}

