package com.fooddelivery.restaurants.dto;

import com.fooddelivery.restaurants.domain.CuisineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private CuisineType cuisine;

}

