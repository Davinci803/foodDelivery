package com.fooddelivery.restaurants.repository;

import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByClosedFalseOrderByRatingDesc();

    List<Restaurant> findByClosedFalseAndCuisineOrderByRatingDesc(CuisineType cuisine);

    List<Restaurant> findByClosedFalseAndRatingGreaterThanEqualOrderByRatingDesc(BigDecimal rating);

    List<Restaurant> findByClosedFalseAndCuisineAndRatingGreaterThanEqualOrderByRatingDesc(CuisineType cuisine, BigDecimal rating);
}

