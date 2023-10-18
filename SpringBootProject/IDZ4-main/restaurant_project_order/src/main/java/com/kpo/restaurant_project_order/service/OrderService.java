package com.kpo.restaurant_project_order.service;

import com.kpo.restaurant_project_order.domain.models.Order;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;

import java.util.List;

public interface OrderService {

    Object makeOrder(String token, Order orderRequestDto) throws InvalidOperationException;

    List<Order> getListOfOrders(String token) throws InvalidOperationException;

    Object changeOrderStatus(String token, Order order) throws InvalidOperationException;

    Object getInfoAboutOrder(String token, Order order) throws InvalidOperationException;
}
