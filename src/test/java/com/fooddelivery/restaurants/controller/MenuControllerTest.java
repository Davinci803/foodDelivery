package com.fooddelivery.restaurants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.menu.domain.MenuItem;
import com.fooddelivery.menu.domain.MenuItemOption;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.menu.dto.MenuItemCreateRequest;
import com.fooddelivery.menu.dto.MenuItemOptionCreateRequest;
import com.fooddelivery.menu.dto.MenuItemOptionUpdateRequest;
import com.fooddelivery.menu.dto.MenuItemUpdateRequest;
import com.fooddelivery.menu.repository.MenuItemOptionRepository;
import com.fooddelivery.menu.repository.MenuItemRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuItemOptionRepository menuItemOptionRepository;

    private Restaurant createRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Menu Restaurant");
        restaurant.setCuisine(CuisineType.ITALIAN);
        restaurant.setRating(new BigDecimal("4.0"));
        return restaurantRepository.save(restaurant);
    }

    @Test
    @DisplayName("POST /restaurants/{id}/menu добавляет блюдо и GET возвращает его в меню")
    void addDishAndGetMenu() throws Exception {
        Restaurant restaurant = createRestaurant();

        MenuItemOptionCreateRequest opt = new MenuItemOptionCreateRequest();
        opt.setName("SMALL");
        opt.setPrice(new BigDecimal("10.00"));
        opt.setPreparationTimeMinutes(15);

        MenuItemCreateRequest request = new MenuItemCreateRequest();
        request.setName("Pizza");
        request.setDescription("Cheese pizza");
        request.setOptions(List.of(opt));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.options.length()").value(1));

        mockMvc.perform(get("/restaurants/{restaurantId}/menu", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    @DisplayName("PUT /menu/{id} обновляет блюдо и его опции")
    void updateDish() throws Exception {
        Restaurant restaurant = createRestaurant();

        MenuItemOptionCreateRequest opt = new MenuItemOptionCreateRequest();
        opt.setName("SMALL");
        opt.setPrice(new BigDecimal("10.00"));
        opt.setPreparationTimeMinutes(15);

        MenuItemCreateRequest create = new MenuItemCreateRequest();
        create.setName("Burger");
        create.setDescription("Beef burger");
        create.setOptions(List.of(opt));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated());

        MenuItem item = menuItemRepository.findAll().getFirst();

        MenuItemOptionCreateRequest newOpt = new MenuItemOptionCreateRequest();
        newOpt.setName("LARGE");
        newOpt.setPrice(new BigDecimal("15.00"));
        newOpt.setPreparationTimeMinutes(20);

        MenuItemUpdateRequest update = new MenuItemUpdateRequest();
        update.setName("Burger Updated");
        update.setDescription("Updated desc");
        update.setOptions(List.of(newOpt));

        mockMvc.perform(put("/menu/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger Updated"))
                .andExpect(jsonPath("$.options.length()").value(1))
                .andExpect(jsonPath("$.options[0].name").value("LARGE"));
    }

    @Test
    @DisplayName("PATCH /menu/{id}/availability и DELETE /menu/{id}")
    void changeAvailabilityAndRemoveDish() throws Exception {
        Restaurant restaurant = createRestaurant();

        MenuItemCreateRequest create = new MenuItemCreateRequest();
        create.setName("Salad");
        create.setDescription("Fresh salad");

        mockMvc.perform(post("/restaurants/{restaurantId}/menu", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated());

        MenuItem item = menuItemRepository.findAll().getFirst();

        mockMvc.perform(patch("/menu/{id}/availability", item.getId())
                        .param("available", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        mockMvc.perform(get("/restaurants/{restaurantId}/menu", restaurant.getId())
                        .param("onlyAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(delete("/menu/{id}", item.getId()))
                .andExpect(status().isNoContent());

        assertThat(menuItemRepository.count()).isZero();
    }

    @Test
    @DisplayName("PUT /menu/options/{optionId} обновляет только выбранную опцию блюда")
    void updateSingleOption() throws Exception {
        Restaurant restaurant = createRestaurant();

        MenuItemOptionCreateRequest opt1 = new MenuItemOptionCreateRequest();
        opt1.setName("SMALL");
        opt1.setPrice(new BigDecimal("10.00"));
        opt1.setPreparationTimeMinutes(15);

        MenuItemOptionCreateRequest opt2 = new MenuItemOptionCreateRequest();
        opt2.setName("MEDIUM");
        opt2.setPrice(new BigDecimal("12.00"));
        opt2.setPreparationTimeMinutes(18);

        MenuItemCreateRequest create = new MenuItemCreateRequest();
        create.setName("Pasta");
        create.setDescription("Bolognese");
        create.setOptions(List.of(opt1, opt2));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated());

        MenuItem item = menuItemRepository.findAll().getFirst();
        List<MenuItemOption> options = item.getOptions();
        assertThat(options).hasSize(2);
        MenuItemOption medium = options.stream()
                .filter(o -> "MEDIUM".equals(o.getName()))
                .findFirst()
                .orElseThrow();

        MenuItemOptionUpdateRequest updateReq = new MenuItemOptionUpdateRequest();
        updateReq.setName("MEDIUM_PLUS");
        updateReq.setPrice(new BigDecimal("13.50"));
        updateReq.setPreparationTimeMinutes(19);

        mockMvc.perform(put("/menu/options/{optionId}", medium.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MEDIUM_PLUS"))
                .andExpect(jsonPath("$.price").value(13.50))
                .andExpect(jsonPath("$.preparationTimeMinutes").value(19));

        MenuItem reloaded = menuItemRepository.findById(item.getId()).orElseThrow();
        assertThat(reloaded.getOptions()).hasSize(2);
        assertThat(reloaded.getOptions().stream().anyMatch(o -> "SMALL".equals(o.getName()))).isTrue();
    }
}

