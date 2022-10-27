package org.example.restaurant.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "restaurant")
public class RestaurantEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "telephone_number")
    private String telephoneNumber;

    @OneToMany(mappedBy= "restaurant"
            , cascade = CascadeType.PERSIST
            , fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "foodType"
            , fetch = FetchType.LAZY)
    private Set<RestaurantFoodTypeEntity> restaurantFoodTypes = new HashSet();
    @Basic
    private LocalDate foundationDate;

    @Basic
    private String address;

    public RestaurantEntity() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantEntity that = (RestaurantEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return 56;
    }
}
