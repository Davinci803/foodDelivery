package com.fooddelivery.users.service;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserProfileHistory;
import com.fooddelivery.users.domain.UserStatus;
import com.fooddelivery.users.repository.UserProfileHistoryRepository;
import com.fooddelivery.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileHistoryRepository historyRepository;

    public UserService(UserRepository userRepository, UserProfileHistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    public User createUser(User user) {
        User created = userRepository.save(user);
        historyRepository.save(UserProfileHistory.from(created));
        return created;
    }

    @Transactional(readOnly = true)
    public User getById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, User updatedData) {
        User existing = getById(id);
        historyRepository.save(UserProfileHistory.from(existing));
        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());
        if (updatedData.getRole() != null) {
            existing.setRole(updatedData.getRole());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }
        return userRepository.save(existing);
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
