package com.fooddelivery.restaurants.repository;

import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // todo Добавить порядок по рейтингу
    List<Restaurant> findByClosedFalse();

    List<Restaurant> findByClosedFalseAndCuisine(CuisineType cuisine);

    List<Restaurant> findByClosedFalseAndRatingGreaterThanEqual(BigDecimal rating);

    List<Restaurant> findByClosedFalseAndCuisineAndRatingGreaterThanEqual(CuisineType cuisine, BigDecimal rating);
}

