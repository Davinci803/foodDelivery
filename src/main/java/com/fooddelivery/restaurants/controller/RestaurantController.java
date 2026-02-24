package com.fooddelivery.restaurants.controller;

import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.dto.RestaurantCreateRequest;
import com.fooddelivery.restaurants.dto.RestaurantResponse;
import com.fooddelivery.restaurants.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurants.mapper.RestaurantMapper;
import com.fooddelivery.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantCreateRequest request) {
        Restaurant restaurant = restaurantMapper.toEntity(request);
        Restaurant created = restaurantService.create(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getListAllRestaurant(
            @RequestParam(required = false) CuisineType cuisine,
            @RequestParam(required = false) BigDecimal minRating
    ) {
        List<Restaurant> restaurants = restaurantService.find(cuisine, minRating);
        List<RestaurantResponse> responses = restaurants.stream()
                .map(restaurantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantUpdateRequest request
    ) {
        Restaurant updated = restaurantService.update(id, request);
        return ResponseEntity.ok(restaurantMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeRestaurant(@PathVariable Long id) {
        restaurantService.close(id);
        return ResponseEntity.noContent().build();
    }
}

