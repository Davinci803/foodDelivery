package com.fooddelivery.restaurants.controller;

import com.fooddelivery.restaurants.dto.MenuItemCreateRequest;
import com.fooddelivery.restaurants.dto.MenuItemResponse;
import com.fooddelivery.restaurants.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/menu")
@RequiredArgsConstructor
public class RestaurantMenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> addDish(@PathVariable Long restaurantId,
                                                    @Valid @RequestBody MenuItemCreateRequest request) {
        MenuItemResponse response = menuService.addDish(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenu(@PathVariable Long restaurantId,
                                                          @RequestParam(defaultValue = "true") boolean onlyAvailable) {
        List<MenuItemResponse> menu = menuService.getRestaurantMenu(restaurantId, onlyAvailable);
        return ResponseEntity.ok(menu);
    }
}

