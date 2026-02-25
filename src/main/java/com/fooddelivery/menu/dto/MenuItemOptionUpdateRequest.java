package com.fooddelivery.menu.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemOptionUpdateRequest {

    private String name;

    @Min(0)
    private BigDecimal price;

    @Min(1)
    private Integer preparationTimeMinutes;
}

