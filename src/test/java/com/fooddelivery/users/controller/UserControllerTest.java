package com.fooddelivery.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserStatus;
import com.fooddelivery.users.dto.UserCreateRequest;
import com.fooddelivery.users.dto.UserUpdateRequest;
import com.fooddelivery.users.repository.UserProfileHistoryRepository;
import com.fooddelivery.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileHistoryRepository historyRepository;

    @Test
    @DisplayName("POST /users создает пользователя и возвращает 201")
    void createUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("David");
        request.setEmail("david@example.com");
        request.setRole(Role.USER);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("David"))
                .andExpect(jsonPath("$.email").value("david@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /users/{id} возвращает созданного пользователя")
    void getUserById() throws Exception {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        mockMvc.perform(get("/users/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("PUT /users/{id} обновляет профиль и пишет историю")
    void updateUserAndHistory() throws Exception {
        User user = new User();
        user.setName("David");
        user.setEmail("david@example.com");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        UserUpdateRequest update = new UserUpdateRequest();
        update.setName("David Updated");
        update.setEmail("david.updated@example.com");
        update.setRole(Role.ADMIN);
        update.setStatus(UserStatus.ACTIVE);

        mockMvc.perform(put("/users/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("David Updated"))
                .andExpect(jsonPath("$.email").value("david.updated@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        assertThat(historyRepository.findByUserIdOrderByRecordedAtDesc(saved.getId()))
                .hasSize(1);
        assertThat(historyRepository.findByUserIdOrderByRecordedAtDesc(saved.getId()).getFirst().getName())
                .isEqualTo("David");
    }

    @Test
    @DisplayName("GET /users/{id}/history?at=... возвращает состояние на дату")
    void getHistoryAt() throws Exception {
        User user = new User();
        user.setName("Dima");
        user.setEmail("dimon@example.com");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        saved.setName("Dima2");
        userRepository.save(saved);

        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String atParam = beforeUpdate.format(formatter);

        mockMvc.perform(get("/users/{id}/history", saved.getId())
                        .param("at", atParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }
}

