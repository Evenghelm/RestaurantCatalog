package org.example.restaurant;

import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.example.restaurant.model.FoodTypeEntity;
import org.example.restaurant.model.RestaurantEntity;
import org.example.restaurant.model.RestaurantFoodTypeEntity;
import org.example.restaurant.model.ReviewEntity;
import org.example.restaurant.repository.FoodTypeRepository;
import org.example.restaurant.repository.RestaurantFoodTypeRepository;
import org.example.restaurant.repository.RestaurantRepository;
import org.example.restaurant.repository.ReviewRepository;
import org.example.restaurant.util.AppContextTest;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "unused"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateAndJPATest extends AppContextTest {
    @Autowired
    private RestaurantRepository restaurantRepository;
    private RestaurantEntity restaurantWithReview;
    private ReviewEntity savedReview;

    @Autowired
    private FoodTypeRepository foodTypeRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeAll
    public void beforeAll() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("test");

        ReviewEntity review1 = new ReviewEntity();
        review1.setReviewText("qqq");
        review1.setRestaurant(restaurant);
        review1.setRating(1);

        ReviewEntity review2 = new ReviewEntity();
        review2.setReviewText("qqq");
        review2.setRestaurant(restaurant);
        review2.setRating(1);

        ReviewEntity review3 = new ReviewEntity();
        review3.setReviewText("qqq");
        review3.setRestaurant(restaurant);
        review3.setRating(1);

        List<ReviewEntity> list = Lists.list(review1, review2, review3);
        restaurant.setReviews(list);
        savedReview = review1;
        restaurantWithReview = restaurantRepository.save(restaurant);
    }

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @Transactional
    public void test1() {
        SQLStatementCountValidator.reset();
        List<ReviewEntity> reviewEntityList = reviewRepository.findAllByRestaurantId(restaurantWithReview.getId());
        for (ReviewEntity e : reviewEntityList) {
            System.out.println(e.getRestaurant().getName());
        }
        /*
         * Сколько запросов типа select будет выполнено. Назначте переменной x правильное значение
         */
        int x = 2;
        //assertSelectCount(x);
        /*
         * Опишите причину:
         * запрос к review, потом к restaurant
         */
    }

    @Test
    @Transactional
    public void test2() {
        SQLStatementCountValidator.reset();
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantWithReview.getId()).get();
        for (ReviewEntity e : restaurant.getReviews()) {
            System.out.println(e.getRestaurant().getName());
        }
        /*
         * Сколько запросов типа select будет выполнено. Назначте переменной x правильное значение
         */
        int x = 2;
        //assertSelectCount(x);
        /*
         * Опишите причину:
         * запрос к restaurant, потом к review
         */
    }

    @Test
    @Transactional
    public void test3() {
        SQLStatementCountValidator.reset();
        Optional<ReviewEntity> byId1 = reviewRepository.findById(savedReview.getId());
        Optional<ReviewEntity> byId2 = reviewRepository.findById(savedReview.getId());
        /*
         * Сколько запросов типа select будет выполнено. Назначте переменной x правильное значение
         */
        int x = 1;
        //assertSelectCount(x);
        /*
         * Объекты byId1 и byId2 одинаковые? Добавьте соответвующий assert
         */

        assertTrue(byId1.get() ==  byId2.get());

        /*
         * Опишите причину:
         * Запрос по id кэшируется, ссылки на один эксземпляр контекста
         */
    }

    @BeforeEach
    public void addFoodType() {
        if(foodTypeRepository.findByName("testGetFoodType") == null) {
            FoodTypeEntity foodTypeEntity = new FoodTypeEntity();
            foodTypeEntity.setName("testGetFoodType");
            foodTypeEntity.setDescription("test");
            foodTypeRepository.save(foodTypeEntity);
        }
    }

    @Test
    @Transactional
    public void test4() {
        SQLStatementCountValidator.reset();
        FoodTypeEntity o1 = foodTypeRepository.findByName("testGetFoodType");
        FoodTypeEntity o2 = foodTypeRepository.findByName("testGetFoodType");
        /*
         * Объекты o1 и o2 одинаковые? Добавьте соответвующий assert
         */

        assertTrue(o1 ==  o2);

        /*
         * Сколько запросов типа select будет выполнено. Назначте переменной x правильное значение
         */
        int x = 2;
        //assertSelectCount(x);
        /*
         * Опишите причину:
         * Запрос не по id Не кэшируются автоматически, ссылки на один эксземпляр контекста
         */
    }

    @Test
    public void test5() {
        Optional<RestaurantEntity> byId1 = restaurantRepository.findById(restaurantWithReview.getId());
        Optional<RestaurantEntity> byId2 = restaurantRepository.findById(restaurantWithReview.getId());
        /*
         * Объекты byId1 и byId2 одинаковые? Добавьте соответвующий assert
         */

        //fail();
        assertFalse(byId1 ==  byId2);

        /*
         * Опишите причину:
         * Разныее сессии > разные сущности в разных контекстах
         */
    }

    @Test
    @Transactional
    void testSessionNotEquals() {
        RestaurantEntity restaurant1 = new RestaurantEntity();
        restaurant1.setName("test");
        restaurant1 = restaurantRepository.save(restaurant1);
        entityManager.detach(restaurant1);
        Optional<RestaurantEntity> byId = restaurantRepository.findById(restaurant1.getId());
        RestaurantEntity restaurant2 = byId.get();

        /*
         * Объекты restaurant1 и restaurant2 одинаковые? Добавьте соответвующий assert
         */

        //fail();
        assertFalse(restaurant1 ==  restaurant2);

        /*
         * Опишите причину:
         * сравнение экземпляров сущностей с разными состояниями
         */
    }

    @Test
    public void test6() {
        Optional<RestaurantEntity> byId = restaurantRepository.findById(restaurantWithReview.getId());
        /*
         * Какая ошибка будет инициирована? Подставьте нужное значение в переменную x
         */
        Class<? extends Throwable> x = LazyInitializationException.class;
        assertThrowsExactly(x, () -> assertEquals(3, byId.get().getReviews().size()));
        /*
         * Опишите причину:
         * Нет сессии - не получится загрузить коллекцию reviews через прокси
         * Как исправить:
         * добавить @Transactional
         */
    }

    @Test
    public void test7() {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName("test 7");
        FoodTypeEntity foodTypeEntity = new FoodTypeEntity();
        foodTypeEntity.setName("test 7");
        foodTypeEntity.setDescription("test 7");
        restaurantEntity.setFoodType(foodTypeEntity);

        /*
         * Какая ошибка будет инициирована? Подставьте нужное значение в переменную x
         */
        Class<? extends Throwable> x = InvalidDataAccessApiUsageException.class;
        assertThrowsExactly(x, () -> restaurantRepository.save(restaurantEntity));
        /*
         * Опишите причину:
         * сущность foodType не сохраняется каскадно вместе с restaurant
         * Как исправить:
         * добавить foodTypeRepository.save(foodTypeEntity);
         * добавить cascade = CascadeType.PERSIST для foodType или @Transactional
         */
    }


}
