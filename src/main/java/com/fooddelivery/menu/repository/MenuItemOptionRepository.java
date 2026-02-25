package com.fooddelivery.menu.repository;

import com.fooddelivery.menu.domain.MenuItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemOptionRepository extends JpaRepository<MenuItemOption, Long> {
}

