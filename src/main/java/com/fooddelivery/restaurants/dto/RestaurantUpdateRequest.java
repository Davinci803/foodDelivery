package com.fooddelivery.restaurants.dto;

import com.fooddelivery.restaurants.domain.CuisineType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantUpdateRequest {

    private String name;
    private CuisineType cuisine;

}

