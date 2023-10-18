package com.kpo.restaurant_project_order.service;

import com.kpo.restaurant_project_order.domain.models.Order;
import com.kpo.restaurant_project_order.domain.models.OrderStatus;
import com.kpo.restaurant_project_order.domain.models.Role;
import com.kpo.restaurant_project_order.domain.repository.DishRepository;
import com.kpo.restaurant_project_order.domain.repository.OrderDishRepository;
import com.kpo.restaurant_project_order.domain.repository.OrderRepository;
import com.kpo.restaurant_project_order.dto.JwtPayload;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;
import com.kpo.restaurant_project_order.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kpo.restaurant_project_order.constants.DefaultCommands.MessagesFromOrder.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final DishRepository dishRepository;

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final OrderDishRepository orderDishRepository;

    /**
     * @param order - order to save in database
     * @return - order with id from database
     * @throws InvalidOperationException - exception if order is null or order is not exist
     */
    public Object makeOrder(String token, Order order) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, WRONG_TOKEN);
        }
        order.getOrderDishes().forEach(orderDish -> {
            var dish = dishRepository.findById(orderDish.getDish().getId()).orElse(null);
            if (dish == null) {
                try {
                    throw new InvalidOperationException(HttpStatus.BAD_REQUEST, DISH_IS_NOT_EXIST);
                } catch (InvalidOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            if (dish.getQuantity() < orderDish.getQuantity()) {
                try {
                    throw new InvalidOperationException(HttpStatus.BAD_REQUEST, DISH_IS_MISSING);
                } catch (InvalidOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            dish.setQuantity(dish.getQuantity() - orderDish.getQuantity());
            dishRepository.save(dish);
        });
        orderRepository.save(order);
        order.getOrderDishes().forEach(orderDish -> {
            orderDish.setOrder(order);
            orderDishRepository.save(orderDish);
        });
        return order;
    }

    /**
     * @return - list of orders
     * @throws InvalidOperationException - exception if token is not valid
     */
    public List<Order> getListOfOrders(String token) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, WRONG_TOKEN);
        }
        return orderRepository.findAll();
    }

    /**
     * @param currOrder - order to change status
     * @return - message about changing status
     * @throws InvalidOperationException - exception if token is not valid or user is not chef
     */
    public String changeOrderStatus(String token, Order currOrder) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload payloadDto = Validator.getPayloadFromToken();
        if (!Objects.equals(payloadDto.getRole(), Role.CHEF)) {
            throw new InvalidOperationException(HttpStatus.FORBIDDEN, "You are not allowed to it");
        }
        Optional<Order> orderOptional = orderRepository.findById(currOrder.getId());
        if (orderOptional.isEmpty()) {
            throw new InvalidOperationException(HttpStatus.NOT_FOUND, "Incorrect Order Id");
        }
        Order order = orderOptional.get();
        try {
            order.setStatus(OrderStatus.valueOf(currOrder.getStatus().name()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, "Wrong role");
        }
        orderRepository.save(order);
        return ORDER_CHANGED;
    }

    /**
     * @param order - order to get info about
     * @return - order with id from database
     * @throws InvalidOperationException - exception if token is not valid
     */
    public Order getInfoAboutOrder(String token, Order order) throws InvalidOperationException {
        Validator.accessToken = token;
        JwtPayload jwtPayload = Validator.getPayload();
        if (jwtPayload == null) {
            throw new InvalidOperationException(HttpStatus.BAD_REQUEST, WRONG_TOKEN);
        }
        return orderRepository.findById(order.getId()).orElse(null);
    }
}
