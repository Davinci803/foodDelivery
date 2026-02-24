package com.fooddelivery.users.service;

import com.fooddelivery.common.exception.EntityNotFoundException;
import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserProfileHistory;
import com.fooddelivery.users.domain.UserStatus;
import com.fooddelivery.users.dto.UserUpdateRequest;
import com.fooddelivery.users.mapper.UserMapper;
import com.fooddelivery.users.repository.UserProfileHistoryRepository;
import com.fooddelivery.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileHistoryRepository historyRepository;
    private final UserMapper userMapper;

    public User createUser(User user) {
        User created = userRepository.save(user);
        historyRepository.save(UserProfileHistory.from(created));
        return created;
    }

    @Transactional(readOnly = true)
    public User getById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        return userRepository.findById(id)
                .map(existing -> {
                    historyRepository.save(UserProfileHistory.from(existing));
                    userMapper.updateEntity(request, existing);
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public void deactivateUser(Long id) {
        User existing = getById(id);
        existing.setStatus(UserStatus.BLOCKED);
        userRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public UserProfileHistory getStateAt(Long userId, Instant at) {
        return historyRepository.findTop1ByUserIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(userId, at)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<UserProfileHistory> getHistory(Long userId) {
        return historyRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }
}
