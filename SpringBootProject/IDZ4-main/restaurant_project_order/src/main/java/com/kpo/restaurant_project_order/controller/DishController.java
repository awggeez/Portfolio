package com.kpo.restaurant_project_order.controller;

import com.kpo.restaurant_project_order.domain.models.Dish;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;
import com.kpo.restaurant_project_order.service.DishServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
public class DishController {

    @Autowired
    private final DishServiceImpl dishService;

    /**
     * @param token - token of user
     * @return - list of dishes or error message
     */
    @RequestMapping("/menu")
    public ResponseEntity<?> getMenu(String token) {
        try {
            return ResponseEntity.ok(dishService.getMenu(token));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    /**
     * @param token - token of user
     * @param dish  - dish request dto
     * @return - dish response dto or error message
     */
    @RequestMapping("/addDish")
    public ResponseEntity<?> addDish(String token, Dish dish) {
        try {
            return ResponseEntity.ok(dishService.addDish(token, dish));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    /**
     * @param token - token of user
     * @param dish  - dish request dto
     * @return - dish response dto or error message
     */
    @RequestMapping("/deleteDish")
    public ResponseEntity<?> deleteDish(String token, Dish dish) {
        try {
            return ResponseEntity.ok(dishService.deleteDish(token, dish));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }
}
