package com.fooddelivery.users.repository;

import com.fooddelivery.users.domain.Role;
import com.fooddelivery.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRole(Role role);
    //Поиск по почте стоит ли?
    Optional<User> findByEmail(String email);
}
