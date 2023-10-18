package com.kpo.restaurant_project_order.service;

import com.kpo.restaurant_project_order.domain.models.Dish;
import com.kpo.restaurant_project_order.domain.models.Role;
import com.kpo.restaurant_project_order.domain.repository.DishRepository;
import com.kpo.restaurant_project_order.dto.DishForUser;
import com.kpo.restaurant_project_order.dto.JwtPayload;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;
import com.kpo.restaurant_project_order.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kpo.restaurant_project_order.constants.DefaultCommands.MessagesFromDish.*;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {

    @Autowired
    private final DishRepository dishRepository;

    /**
     * @return - list of dishes
     * @throws InvalidOperationException - exception if token is invalid or user is not manager
     */
    public List<Dish> getMenu(String token) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, INVALID_TOKEN);
        }
        var dishes = dishRepository.findAll();
        Role role = jwtPayload.getRole();
        if (role == Role.MANAGER) {
            return dishes;
        } else {
            return dishes.stream().map(dish -> new DishForUser()
                    .setExist(dish.getQuantity() > 0)
                    .setPrice(dish.getPrice())
                    .setName(dish.getName())
                    .setDescription(dish.getDescription())
            ).collect(toList());
        }
    }

    /**
     * @param token - token of user or manager
     * @param dish  - dish to add
     * @return - message about adding dish
     * @throws InvalidOperationException - exception if token is invalid or user is not manager
     */
    public String addDish(String token, Dish dish) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, INVALID_TOKEN);
        }
        Role role = jwtPayload.getRole();
        if (role != Role.MANAGER) {
            throw new InvalidOperationException(HttpStatus.FORBIDDEN, NO_RIGHTS);
        }
        Dish currentDish = dishRepository.findByName(dish.getName());
        if (currentDish != null) {
            currentDish.setDescription(dish.getDescription())
                    .setPrice(dish.getPrice())
                    .setQuantity(dish.getQuantity());
            dishRepository.save(currentDish);
            return MENU_UPDATED;
        }
        dishRepository.save(new Dish()
                .setName(dish.getName())
                .setDescription(dish.getDescription())
                .setPrice(dish.getPrice())
                .setQuantity(dish.getQuantity()));
        return DISH_ADDED;
    }

    /**
     * @param token - token of user or manager
     * @param dish  - dish to delete
     * @return - message about deleting dish
     * @throws InvalidOperationException - exception if token is invalid or user is not manager
     */
    public String deleteDish(String token, Dish dish) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, INVALID_TOKEN);
        }
        Role role = jwtPayload.getRole();
        if (role != Role.MANAGER) {
            throw new InvalidOperationException(HttpStatus.FORBIDDEN, NO_RIGHTS);
        }
        Dish currentDish = dishRepository.findByName(dish.getName());
        if (currentDish == null) {
            throw new InvalidOperationException(HttpStatus.NOT_FOUND, DISH_NOT_FOUND);
        }
        dishRepository.delete(currentDish);
        return DISH_DELETED;
    }
}
