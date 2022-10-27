package org.example.restaurant.repository;

import org.example.restaurant.model.RestaurantFoodTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantFoodTypeRepository extends JpaRepository<RestaurantFoodTypeEntity, Long> {
}
