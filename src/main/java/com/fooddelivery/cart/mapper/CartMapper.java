package com.fooddelivery.cart.mapper;

import com.fooddelivery.cart.domain.Cart;
import com.fooddelivery.cart.domain.CartItem;
import com.fooddelivery.cart.dto.CartItemResponse;
import com.fooddelivery.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    CartResponse toResponse(Cart cart);

    @Mapping(target = "menuItemOptionId", source = "menuItemOption.id")
    @Mapping(target = "menuItemName", source = "menuItemOption.menuItem.name")
    @Mapping(target = "optionName", source = "menuItemOption.name")
    CartItemResponse toItemResponse(CartItem item);

    List<CartItemResponse> toItemResponses(List<CartItem> items);
}

