package com.fooddelivery.restaurants.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private boolean available;
    private List<MenuItemOptionResponse> options;
}

