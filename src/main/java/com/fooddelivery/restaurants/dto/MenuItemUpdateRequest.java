package com.fooddelivery.restaurants.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuItemUpdateRequest {

    private String name;
    private String description;

    @Valid
    private List<MenuItemOptionCreateRequest> options;
}

