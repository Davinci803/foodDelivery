package com.fooddelivery.restaurants.dto;

import com.fooddelivery.restaurants.domain.CuisineType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RestaurantResponse {

    private Long id;
    private String name;
    private CuisineType cuisine;
    private BigDecimal rating;
    private boolean closed;

}

