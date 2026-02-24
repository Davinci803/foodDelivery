package com.fooddelivery.restaurants.service;

import com.fooddelivery.common.exception.EntityNotFoundException;
import com.fooddelivery.common.exception.RestaurantAlreadyClosedException;
import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurants.mapper.RestaurantMapper;
import com.fooddelivery.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Transactional(readOnly = true)
    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
    }

    @Transactional(readOnly = true)
    public List<Restaurant> find(CuisineType cuisine, BigDecimal minRating) {
        if (cuisine != null && minRating != null) {
            return restaurantRepository.findByClosedFalseAndCuisineAndRatingGreaterThanEqualOrderByRatingDesc(cuisine, minRating);
        } else if (cuisine != null) {
            return restaurantRepository.findByClosedFalseAndCuisineOrderByRatingDesc(cuisine);
        } else if (minRating != null) {
            return restaurantRepository.findByClosedFalseAndRatingGreaterThanEqualOrderByRatingDesc(minRating);
        } else {
            return restaurantRepository.findByClosedFalseOrderByRatingDesc();
        }
    }

    public Restaurant update(Long id, RestaurantUpdateRequest request) {
        return restaurantRepository.findById(id)
                .map(existing -> {
                    restaurantMapper.updateEntity(request, existing);
                    return restaurantRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
    }


    public void close(Long id) {
        Restaurant existing = getById(id);
        if (existing.isClosed()) {
            throw new RestaurantAlreadyClosedException(id);
        }
        existing.setClosed(true);
        restaurantRepository.save(existing);
    }
}

