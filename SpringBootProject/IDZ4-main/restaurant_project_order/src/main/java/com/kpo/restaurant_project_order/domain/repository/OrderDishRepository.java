package com.kpo.restaurant_project_order.domain.repository;

import com.kpo.restaurant_project_order.domain.models.OrderDish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDishRepository extends JpaRepository<OrderDish, Integer> {
}
