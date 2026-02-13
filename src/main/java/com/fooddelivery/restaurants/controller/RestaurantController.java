package com.fooddelivery.restaurants.controller;

import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.dto.RestaurantCreateRequest;
import com.fooddelivery.restaurants.dto.RestaurantResponse;
import com.fooddelivery.restaurants.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// todo эдвайсер написать для обработки исключений
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // Админ или владелец ресторана
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant();
        // todo маппер интрефейс и отельный контроллер эдвацйс(провесить аннотацию)
        restaurant.setName(request.getName());
        restaurant.setCuisine(request.getCuisine());
        Restaurant created = restaurantService.create(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    //Все
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        return ResponseEntity.ok(toResponse(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getListAllRestaurant(
            @RequestParam(required = false) CuisineType cuisine,
            @RequestParam(required = false) BigDecimal minRating
    ) {
        List<Restaurant> restaurants = restaurantService.find(cuisine, minRating);
        List<RestaurantResponse> responses = restaurants.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    //todo ручку разбить, ловить, патч?
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantUpdateRequest request
    ) {
        Restaurant updatedData = new Restaurant();
        updatedData.setName(request.getName());
        updatedData.setCuisine(request.getCuisine());
        Restaurant updated = restaurantService.update(id, updatedData);
        return ResponseEntity.ok(toResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeRestaurant(@PathVariable Long id) {
        restaurantService.close(id);
        return ResponseEntity.noContent().build();
    }

    // todo нормально сделать
    private RestaurantResponse toResponse(Restaurant restaurant) {
        RestaurantResponse r = new RestaurantResponse();
        r.setId(restaurant.getId());
        r.setName(restaurant.getName());
        r.setCuisine(restaurant.getCuisine());
        r.setRating(restaurant.getRating());
        r.setClosed(restaurant.isClosed());
        return r;
    }
}

