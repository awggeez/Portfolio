package com.kpo.restaurant_project_order.domain.repository;

import com.kpo.restaurant_project_order.domain.models.Dish;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Integer> {
    boolean existsByName(String name);

    Dish findByName(String name);

    @Transactional
    void deleteByName(String dishName);
}
