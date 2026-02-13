package com.fooddelivery.restaurants.service;

import com.fooddelivery.restaurants.domain.CuisineType;
import com.fooddelivery.restaurants.domain.Restaurant;
import com.fooddelivery.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
// todo везде так сделать
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    // todo подумать о транзакции
    @Transactional(readOnly = true)
    public Restaurant getById(Long id) {
        // todo мб делать контрллер эдвайс исключение свою прописать и ловаить в контроллерах
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    @Transactional(readOnly = true)
    public List<Restaurant> find(CuisineType cuisine, BigDecimal minRating) {
        // todo Над if подумать. по сервисам разбить, через стримы
        if (cuisine != null && minRating != null) {
            return restaurantRepository.findByClosedFalseAndCuisineAndRatingGreaterThanEqual(cuisine, minRating);
        } else if (cuisine != null) {
            return restaurantRepository.findByClosedFalseAndCuisine(cuisine);
        } else if (minRating != null) {
            return restaurantRepository.findByClosedFalseAndRatingGreaterThanEqual(minRating);
        } else {
            return restaurantRepository.findByClosedFalse();
        }
    }

    // todo optinal, обернуть исключение , в эдвайсере обработать, MapStruct
    public Restaurant update(Long id, Restaurant updatedData) {
        Restaurant existing = getById(id);
        // todo Над if подумать
        if (updatedData.getName() != null) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getCuisine() != null) {
            existing.setCuisine(updatedData.getCuisine());
        }
        // todo ошибка вылететь может, свою написать
        return restaurantRepository.save(existing);
    }


    public void close(Long id) {
        // todo проверка что рестик закрыт, опять мб свое исключние напить и тд
        Restaurant existing = getById(id);
        existing.setClosed(true);
        restaurantRepository.save(existing);
    }
}

