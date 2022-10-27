package org.example.restaurant.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "restaurant_food_type")
public class RestaurantFoodTypeEntity {
    @EmbeddedId
    private RestaurantMainFoodKey id = new RestaurantMainFoodKey();

    @ManyToOne (fetch = FetchType.LAZY)
    @MapsId("restaurantId")
    private RestaurantEntity restaurant;

    @ManyToOne (fetch = FetchType.LAZY)
    @MapsId("foodTypeId")
    private FoodTypeEntity foodType;

    @Basic
    @Column(nullable = false)
    private boolean isMainFoodType;

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
        restaurant.getRestaurantFoodTypes().add(this);
    }
}
