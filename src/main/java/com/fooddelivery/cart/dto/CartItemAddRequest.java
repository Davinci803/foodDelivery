package com.fooddelivery.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemAddRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long menuItemOptionId;

    @Min(1)
    private int quantity;
}

