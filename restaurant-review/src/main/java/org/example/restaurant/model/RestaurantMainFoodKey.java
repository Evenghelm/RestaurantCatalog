package org.example.restaurant.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class RestaurantMainFoodKey implements Serializable {
    @Column (name = "restaurant_id")
    private Long restaurantId;
    @Column (name = "food_type_id")
    private Long foodTypeId;
}
