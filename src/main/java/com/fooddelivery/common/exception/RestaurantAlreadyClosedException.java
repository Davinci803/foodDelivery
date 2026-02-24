package com.fooddelivery.common.exception;

public class RestaurantAlreadyClosedException extends RuntimeException {

    public RestaurantAlreadyClosedException(Long id) {
        super(String.format("Restaurant with id %d is already closed", id));
    }
}
