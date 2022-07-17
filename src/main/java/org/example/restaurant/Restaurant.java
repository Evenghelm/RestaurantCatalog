package org.example.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.restaurant.repository")
public class Restaurant {
    public static void main(String[] args) {
        SpringApplication.run(Restaurant.class, args);
    }
}
