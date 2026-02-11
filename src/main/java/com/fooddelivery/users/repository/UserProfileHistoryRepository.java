package com.fooddelivery.users.repository;

import com.fooddelivery.users.domain.UserProfileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


public interface UserProfileHistoryRepository extends JpaRepository<UserProfileHistory, Long> {
    //подумать мб квери сделать
    Optional<UserProfileHistory> findTop1ByUserIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(Long userId, Instant at);

    List<UserProfileHistory> findByUserIdOrderByRecordedAtDesc(Long userId);
}
