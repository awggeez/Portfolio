package com.kpo.restaurant_project_order.service;

import com.kpo.restaurant_project_order.domain.models.Dish;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;

import java.util.List;

public interface DishService {

    List<Dish> getMenu(String token) throws InvalidOperationException;

    String addDish(String token, Dish dish) throws InvalidOperationException;

    String deleteDish(String token, Dish dish) throws InvalidOperationException;
}
