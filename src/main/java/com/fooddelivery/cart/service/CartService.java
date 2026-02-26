package com.fooddelivery.cart.service;

import com.fooddelivery.cart.domain.Cart;
import com.fooddelivery.cart.domain.CartItem;
import com.fooddelivery.cart.dto.CartItemAddRequest;
import com.fooddelivery.cart.dto.CartItemUpdateRequest;
import com.fooddelivery.cart.dto.CartResponse;
import com.fooddelivery.cart.mapper.CartMapper;
import com.fooddelivery.cart.repository.CartItemRepository;
import com.fooddelivery.cart.repository.CartRepository;
import com.fooddelivery.common.exception.EntityNotFoundException;
import com.fooddelivery.menu.domain.MenuItemOption;
import com.fooddelivery.menu.repository.MenuItemOptionRepository;
import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuItemOptionRepository menuItemOptionRepository;
    private final CartMapper cartMapper;

    public CartResponse addItem(CartItemAddRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", request.getUserId()));

        MenuItemOption option = menuItemOptionRepository.findById(request.getMenuItemOptionId())
                .orElseThrow(() -> new EntityNotFoundException("MenuItemOption", request.getMenuItemOptionId()));

        Cart cart = cartRepository.findByUserAndActiveIsTrue(user)
                .orElseGet(() -> createCart(user, option));

        if (!cart.getRestaurant().getId().equals(option.getMenuItem().getRestaurant().getId())) {
            throw new IllegalStateException("Cart can contain items only from one restaurant");
        }

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getMenuItemOption().getId().equals(option.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setMenuItemOption(option);
                    ci.setQuantity(0);
                    ci.setUnitPrice(option.getPrice());
                    ci.setLineTotal(BigDecimal.ZERO);
                    cart.getItems().add(ci);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + request.getQuantity());
        recalcLineTotal(item);
        recalcCart(cart);

        Cart saved = cartRepository.save(cart);
        CartResponse response = cartMapper.toResponse(saved);
        response.setItems(cartMapper.toItemResponses(saved.getItems()));
        return response;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        Cart cart = cartRepository.findByUserAndActiveIsTrue(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart for user", userId));

        CartResponse response = cartMapper.toResponse(cart);
        response.setItems(cartMapper.toItemResponses(cart.getItems()));
        return response;
    }

    public CartResponse updateItem(Long itemId, CartItemUpdateRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem", itemId));
        Cart cart = item.getCart();

        if (request.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            recalcLineTotal(item);
        }

        recalcCart(cart);
        Cart saved = cartRepository.save(cart);
        CartResponse response = cartMapper.toResponse(saved);
        response.setItems(cartMapper.toItemResponses(saved.getItems()));
        return response;
    }

    public void removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem", itemId));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        recalcCart(cart);
        cartRepository.save(cart);
        if (cart.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
            clearCart(cart.getUser().getId());
        }
    }

    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        cartRepository.findByUserAndActiveIsTrue(user)
                .ifPresent(cartRepository::delete);
    }

    private Cart createCart(User user, MenuItemOption option) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setRestaurant(option.getMenuItem().getRestaurant());
        cart.setActive(true);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setEtaMinutes(estimateEta(option));
        return cart;
    }

    private void recalcLineTotal(CartItem item) {
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    private void recalcCart(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cart.setEtaMinutes(estimateEtaForCart(cart));

    }

    //todo пока заглушка, потом загруженность курьеров добавить
    private int estimateEta(MenuItemOption option) {
        return 30 + option.getPreparationTimeMinutes();
    }

    private int estimateEtaForCart(Cart cart) {
        int prep = cart.getItems().stream()
                .mapToInt(ci -> ci.getMenuItemOption().getPreparationTimeMinutes())
                .max()
                .orElse(0);
        return 30 + prep;
    }
}

