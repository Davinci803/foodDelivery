package com.fooddelivery.menu.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemOptionResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private int preparationTimeMinutes;
}

