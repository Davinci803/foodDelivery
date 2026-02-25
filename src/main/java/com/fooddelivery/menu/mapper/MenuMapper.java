package com.fooddelivery.menu.mapper;

import com.fooddelivery.menu.domain.MenuItem;
import com.fooddelivery.menu.domain.MenuItemOption;
import com.fooddelivery.menu.dto.MenuItemCreateRequest;
import com.fooddelivery.menu.dto.MenuItemOptionCreateRequest;
import com.fooddelivery.menu.dto.MenuItemOptionResponse;
import com.fooddelivery.menu.dto.MenuItemOptionUpdateRequest;
import com.fooddelivery.menu.dto.MenuItemResponse;
import com.fooddelivery.menu.dto.MenuItemUpdateRequest;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    void updateOption(MenuItemOptionUpdateRequest request, @MappingTarget MenuItemOption option);

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


