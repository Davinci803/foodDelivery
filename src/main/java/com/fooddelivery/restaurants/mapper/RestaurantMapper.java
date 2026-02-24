package com.fooddelivery.restaurants.mapper;

import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.dto.RestaurantCreateRequest;
import com.fooddelivery.restaurants.dto.RestaurantResponse;
import com.fooddelivery.restaurants.dto.RestaurantUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "closed", constant = "false")

    Restaurant toEntity(RestaurantCreateRequest request);
    RestaurantResponse toResponse(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "closed", ignore = true)

    void updateEntity(RestaurantUpdateRequest request, @MappingTarget Restaurant restaurant);
}
