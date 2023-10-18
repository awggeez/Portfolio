package com.kpo.restaurant_project_order.domain.repository;

import com.kpo.restaurant_project_order.domain.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
