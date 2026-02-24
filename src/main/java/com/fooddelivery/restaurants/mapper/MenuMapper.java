package com.fooddelivery.restaurants.mapper;

import com.fooddelivery.restaurants.domain.MenuItem;
import com.fooddelivery.restaurants.domain.MenuItemOption;
import com.fooddelivery.restaurants.dto.MenuItemCreateRequest;
import com.fooddelivery.restaurants.dto.MenuItemOptionCreateRequest;
import com.fooddelivery.restaurants.dto.MenuItemOptionResponse;
import com.fooddelivery.restaurants.dto.MenuItemResponse;
import com.fooddelivery.restaurants.dto.MenuItemUpdateRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MenuMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "options", ignore = true)
    MenuItem toEntity(MenuItemCreateRequest request);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "id", ignore = true)
    MenuItemOption toOptionEntity(MenuItemOptionCreateRequest request);

    MenuItemResponse toResponse(MenuItem item);

    MenuItemOptionResponse toOptionResponse(MenuItemOption option);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateEntity(MenuItemUpdateRequest request, @MappingTarget MenuItem item);

    @AfterMapping
    default void fillOptions(MenuItemCreateRequest request, @MappingTarget MenuItem item) {
        if (request.getOptions() == null || request.getOptions().isEmpty()) {
            item.setOptions(new ArrayList<>());
            return;
        }
        List<MenuItemOption> options = new ArrayList<>();
        for (MenuItemOptionCreateRequest optReq : request.getOptions()) {
            MenuItemOption option = toOptionEntity(optReq);
            option.setMenuItem(item);
            options.add(option);
        }
        item.setOptions(options);
    }

    @AfterMapping
    default void updateOptions(MenuItemUpdateRequest request, @MappingTarget MenuItem item) {
        if (request.getOptions() == null) {
            return;
        }
        item.getOptions().clear();
        List<MenuItemOption> options = new ArrayList<>();
        for (MenuItemOptionCreateRequest optReq : request.getOptions()) {
            MenuItemOption option = toOptionEntity(optReq);
            option.setMenuItem(item);
            options.add(option);
        }
        item.setOptions(options);
    }
}

