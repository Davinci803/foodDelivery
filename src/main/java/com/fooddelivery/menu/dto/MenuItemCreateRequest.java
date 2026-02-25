package com.fooddelivery.menu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuItemCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @Valid
    private List<MenuItemOptionCreateRequest> options;
}

