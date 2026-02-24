package com.fooddelivery.restaurants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.dto.RestaurantCreateRequest;
import com.fooddelivery.restaurants.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("POST /restaurants создает ресторан")
    void createRestaurant() throws Exception {
        RestaurantCreateRequest request = new RestaurantCreateRequest();
        request.setName("Pasta House");
        request.setCuisine(CuisineType.ITALIAN);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Pasta House"))
                .andExpect(jsonPath("$.cuisine").value("ITALIAN"));
    }

    @Test
    @DisplayName("GET /restaurants/{id} возвращает ресторан")
    void getRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Sushi Bar");
        restaurant.setCuisine(CuisineType.JAPANESE);
        restaurant.setRating(new BigDecimal("4.5"));
        Restaurant saved = restaurantRepository.save(restaurant);

        mockMvc.perform(get("/restaurants/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Sushi Bar"))
                .andExpect(jsonPath("$.cuisine").value("JAPANESE"));
    }

    @Test
    @DisplayName("GET /restaurants с фильтрами по кухне и рейтингу")
    void listRestaurantsWithFilters() throws Exception {
        Restaurant r1 = new Restaurant();
        r1.setName("R1");
        r1.setCuisine(CuisineType.ITALIAN);
        r1.setRating(new BigDecimal("4.0"));

        Restaurant r2 = new Restaurant();
        r2.setName("R2");
        r2.setCuisine(CuisineType.ITALIAN);
        r2.setRating(new BigDecimal("3.0"));

        Restaurant r3 = new Restaurant();
        r3.setName("R3");
        r3.setCuisine(CuisineType.JAPANESE);
        r3.setRating(new BigDecimal("5.0"));

        restaurantRepository.save(r1);
        restaurantRepository.save(r2);
        restaurantRepository.save(r3);

        mockMvc.perform(get("/restaurants")
                        .param("cuisine", "ITALIAN")
                        .param("minRating", "3.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("R1"));
    }

    @Test
    @DisplayName("PUT /restaurants/{id} обновляет ресторан")
    void updateRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Old Name");
        restaurant.setCuisine(CuisineType.AMERICAN);
        restaurant.setRating(new BigDecimal("3.0"));
        Restaurant saved = restaurantRepository.save(restaurant);

        RestaurantUpdateRequest request = new RestaurantUpdateRequest();
        request.setName("New Name");

        mockMvc.perform(put("/restaurants/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    @DisplayName("DELETE /restaurants/{id} закрывает ресторан")
    void closeRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("To Close");
        restaurant.setCuisine(CuisineType.OTHER);
        restaurant.setRating(new BigDecimal("4.0"));
        Restaurant saved = restaurantRepository.save(restaurant);

        mockMvc.perform(delete("/restaurants/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        Restaurant reloaded = restaurantRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.isClosed()).isTrue();
    }
}

