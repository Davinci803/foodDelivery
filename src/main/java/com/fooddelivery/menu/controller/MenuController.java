package com.fooddelivery.menu.controller;

import com.fooddelivery.menu.dto.MenuItemOptionResponse;
import com.fooddelivery.menu.dto.MenuItemOptionUpdateRequest;
import com.fooddelivery.menu.dto.MenuItemResponse;
import com.fooddelivery.menu.dto.MenuItemUpdateRequest;
import com.fooddelivery.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateDish(@PathVariable Long id,
                                                       @Valid @RequestBody MenuItemUpdateRequest request) {
        MenuItemResponse response = menuService.updateDish(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDish(@PathVariable Long id) {
        menuService.removeDish(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<MenuItemResponse> changeAvailability(@PathVariable Long id,
                                                               @RequestParam boolean available) {
        MenuItemResponse response = menuService.changeAvailability(id, available);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<MenuItemOptionResponse> updateOption(
            @PathVariable Long optionId,
            @Valid @RequestBody MenuItemOptionUpdateRequest request
    ) {
        MenuItemOptionResponse response = menuService.updateOption(optionId, request);
        return ResponseEntity.ok(response);
    }
}

