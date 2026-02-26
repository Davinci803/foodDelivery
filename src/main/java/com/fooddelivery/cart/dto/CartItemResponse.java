package com.fooddelivery.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {

    private Long id;
    private Long menuItemOptionId;
    private String menuItemName;
    private String optionName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}

