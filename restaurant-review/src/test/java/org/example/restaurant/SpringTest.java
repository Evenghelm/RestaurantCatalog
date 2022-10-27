package org.example.restaurant;

import org.example.restaurant.abc.A;
import org.example.restaurant.abc.B;
import org.example.restaurant.abc.C;
import org.example.restaurant.model.RestaurantEntity;
import org.example.restaurant.repository.RestaurantRepository;
import org.example.restaurant.service.Service;
import org.example.restaurant.service.ServiceForTest;
import org.example.restaurant.service.impl.SampleBeanA;
import org.example.restaurant.service.impl.StringReader;
import org.example.restaurant.util.AppContextTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpringTest extends AppContextTest {
    @Autowired
    private Service service;

    @Test
    public void test1() {
        /*
         * Какой тип объектов.
         * Подставьте правильное значение в переменные x и y
         */
        Class<?> x = StringReader.class;
        Class<?> y = SampleBeanA.class;
        assertInstanceOf(x, service.getObjectReader());
        assertInstanceOf(y, service.getSampleBean());
        /*
         * Опишите причину:
         * коллизию для ObjectReader разрешает профиль,
         * для SampleBean - квалификатор
         */
    }

    @Autowired
    private A a;

    @Autowired
    private B b;

    @Autowired
    private C c;
    private static boolean test2_1_finish = false;

    @Test
    @Order(1)
    @DirtiesContext
    public void test2_1() {
        /*
         * Одинаковые ли объекты a и b.getA()?
         * Одинаковые ли объекты b.getA() и c.getA()?
         * Добавьте соответствующие assert
         */

        //fail();
        assertFalse(a == b.getA());
        assertFalse(b.getA() == c.getA());

        /*
         * Опишите причину:
         * разные экземпляры бинов (prototype scope)
         */
        test2_1_finish = true;
    }

    @Test
    @Order(2)
    public void test2_2() {
        /*
         * Сколько раз были вызваны фунции postConstruct и preDestroy?
         * Подставьте правильное значение в переменные x, y, z, w.
         */

        if(test2_1_finish) {
            int x = 6, y = 0, z = 2, w = 1;
            assertEquals(x, A.postConstructCount.get());
            assertEquals(y, A.preDestroyCount.get());
            assertEquals(z, B.postConstructCount.get());
            assertEquals(w, B.preDestroyCount.get());
        } else {
            int x = 3, y = 0, z = 1, w = 0;
            assertEquals(x, A.postConstructCount.get());
            assertEquals(y, A.preDestroyCount.get());
            assertEquals(z, B.postConstructCount.get());
            assertEquals(w, B.preDestroyCount.get());
        }
        /*
         * Опишите причину:
         * при test2_1_finish кол-во созданий в 2 раза больше из-за сброса контекста
         * На что влияет @DirtiesContext?:
         * Очистка контекста после выполнения грязного действия > вызов destroy() для singleton бинов
         */
    }

    @Autowired
    ServiceForTest serviceForTest;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void test8() {
        try {
            serviceForTest.test8("test8");
        } catch (RuntimeException e) {
            System.out.println("error");
        }
        RestaurantEntity byId = restaurantRepository.findFirstByName("test8");
        /*
         * Будет ли сохранен test8 в методе test8?
         * Добавьте соответствующий assert
         */
        //fail();
        assertNotNull(byId); //без Transactional операция не откатится
    }
    @Test
    public void test9() {
        try {
            serviceForTest.test9("test8");
        } catch (RuntimeException e) {
            System.out.println("error");
        }
        RestaurantEntity byId = restaurantRepository.findFirstByName("test8");

        /*
         * Будет ли сохранен test9 в методе test8?
         * Добавьте соответствующий assert
         */
        //fail();
        assertNotNull(byId); //через прокси не получится откатить результат вложенного метода
    }
    @Test
    public void test10() {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName("test10");
        restaurantRepository.save(restaurantEntity);
        serviceForTest.test10("test10");
        Optional<RestaurantEntity> byId = restaurantRepository.findById(restaurantEntity.getId());
        assertTrue(byId.isPresent());
        /*
         * Будет ли изменен test10 в методе test10?
         * Присвойте перенной x нужное значение
         */
        String x = "test10Updated";
        assertEquals(x, byId.get().getName());
    }

    @Test
    public void test11() {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName("test11");
        restaurantRepository.save(restaurantEntity);
        try {
            serviceForTest.test11("test11");
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        Optional<RestaurantEntity> byId = restaurantRepository.findById(restaurantEntity.getId());
        assertTrue(byId.isPresent());
        /*
         * Будет ли изменен test11 в методе test11?
         * Присвойте перенной x нужное значение
         */
        String x = "test11";
        assertEquals(x, byId.get().getName());
    }
}
