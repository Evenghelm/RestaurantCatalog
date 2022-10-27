package org.example.restaurant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ParameterCanBeLocal", "unused", "StringOperationCanBeSimplified", "UnusedAssignment"})
public class JavaCoreTest {
    @Test
    void test1() {
        String a = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String b = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        /*
         * Объекты a и b одинаковые? Добавьте соответвующий assert
         */

        //fail();
        assertTrue(a ==  b);

        /*
         * Опишите причину:
         * Пул строк
         */
    }

    @Test
    void test2() {
        String a = "aaa";
        String b = new String ("aaa") ;
        /*
         * Объекты a и b одинаковые? Добавьте соответвующий assert
         */

        //fail();
        assertFalse(a ==  b);

        /*
         * Опишите причину:
         * Разные ссылки
         */
    }

    @Test
    void test3() {
        String a = "AAA";
        String b = a.toLowerCase() ;
        /*
         * Изменится ли объект a? Добавьте соответвующий assert
         */

        //fail();
        assertNotEquals("aaa", a);

        /*
         * Опишите причину:
         * toLowerCase не изменит оригинал
         */

    }

    ////
    @Test
    void test4() {
        Integer a = 5000;
        Integer b = 5000;
        /*
         * Объекты a и b одинаковые? Добавьте соответвующий assert
         */

        //fail();
        assertFalse(a ==  b);

        /*
         * Опишите причину:
         * В пул помещаются только числа в диапазоне -128:127 > будут разные объекты
         */
    }

    @Test
    void test5() {
        double a = 10.1;
        double b = a;
        a *= 10;
        /*
         * Изменится ли значение переменной b? Добавьте соответвующий assert
         */
        //fail();
        assertNotEquals(101.0, b);
    }

    static class A {
        int a;
    }

    private void f(A a) {
        a.a *= 10;
    }

    @Test
    public void test6() {
        A a = new A();
        a.a = 10;
        f(a);
        /*
         * Изменится ли значение a.a? Добавьте соответвующий assert
         */
        //fail();
        assertEquals(100, a.a);
    }

    private void f2(A a) {
        a = new A();
        a.a = 100;
    }

    @Test
    public void test7() {
        A a = new A();
        a.a = 10;
        f2(a);
        /*
         * Изменится ли значение a.a? Добавьте соответвующий assert
         */
        //fail();
        assertNotEquals(100, a.a);

    }

    ///
    @Test
    public void test8() {
        double a1 = 0;
        for (int i = 1; i <= 10; i++) {
            a1 += 0.1;
        }
        double a2 = 0.1 * 10;
        /*
         * Одинаковое ли значение переменных a1 и a2? Добавьте соответвующий assert
         */
        //fail();
        assertNotEquals(a1, a2);

        /*
         * Опишите причину:
         * Потеря точности при сложении
         */
    }
}
