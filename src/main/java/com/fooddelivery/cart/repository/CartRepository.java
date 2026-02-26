package com.fooddelivery.cart.repository;

import com.fooddelivery.cart.domain.Cart;
import com.fooddelivery.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndActiveIsTrue(User user);
}

