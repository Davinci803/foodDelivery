package com.fooddelivery.menu.service;

import com.fooddelivery.common.exception.EntityNotFoundException;
import com.fooddelivery.menu.domain.MenuItem;
import com.fooddelivery.menu.domain.MenuItemOption;
import com.fooddelivery.menu.dto.MenuItemCreateRequest;
import com.fooddelivery.menu.dto.MenuItemOptionResponse;
import com.fooddelivery.menu.dto.MenuItemOptionUpdateRequest;
import com.fooddelivery.menu.dto.MenuItemResponse;
import com.fooddelivery.menu.dto.MenuItemUpdateRequest;
import com.fooddelivery.menu.mapper.MenuMapper;
import com.fooddelivery.menu.repository.MenuItemOptionRepository;
import com.fooddelivery.menu.repository.MenuItemRepository;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemOptionRepository menuItemOptionRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuMapper menuMapper;

    public MenuItemResponse addDish(Long restaurantId, MenuItemCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        MenuItem item = menuMapper.toEntity(request);
        item.setRestaurant(restaurant);
        MenuItem saved = menuItemRepository.save(item);
        return menuMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getRestaurantMenu(Long restaurantId, boolean onlyAvailable) {
        List<MenuItem> items = onlyAvailable
                ? menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId)
                : menuItemRepository.findByRestaurantId(restaurantId);
        return items.stream()
                .map(menuMapper::toResponse)
                .toList();
    }

    public MenuItemResponse updateDish(Long id, MenuItemUpdateRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem", id));
        menuMapper.updateEntity(request, item);
        MenuItem saved = menuItemRepository.save(item);
        return menuMapper.toResponse(saved);
    }

    public void removeDish(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new EntityNotFoundException("MenuItem", id);
        }
        menuItemRepository.deleteById(id);
    }

    public MenuItemResponse changeAvailability(Long id, boolean available) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem", id));
        item.setAvailable(available);
        MenuItem saved = menuItemRepository.save(item);
        return menuMapper.toResponse(saved);
    }

    public MenuItemOptionResponse updateOption(Long optionId, MenuItemOptionUpdateRequest request) {
        MenuItemOption option = menuItemOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItemOption", optionId));
        menuMapper.updateOption(request, option);
        MenuItemOption saved = menuItemOptionRepository.save(option);
        return menuMapper.toOptionResponse(saved);
    }
}

