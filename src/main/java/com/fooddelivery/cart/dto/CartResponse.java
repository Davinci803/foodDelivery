package com.fooddelivery.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponse {

    private Long id;
    private Long userId;
    private Long restaurantId;
    private BigDecimal totalPrice;
    private Integer etaMinutes;
    private List<CartItemResponse> items;
}

