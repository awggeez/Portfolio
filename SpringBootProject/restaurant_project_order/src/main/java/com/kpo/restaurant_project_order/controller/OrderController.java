package com.kpo.restaurant_project_order.controller;

import com.kpo.restaurant_project_order.domain.models.Order;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;
import com.kpo.restaurant_project_order.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderServiceImpl orderService;

    /**
     * @param token           - token of user
     * @param orderRequestDto - order request dto
     * @return - order response dto or error message
     */
    @RequestMapping("/makeOrder")
    public ResponseEntity<?> makeOrder(String token, Order orderRequestDto) {
        try {
            return ResponseEntity.ok(orderService.makeOrder(token, orderRequestDto));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    /**
     * @param token - token of user
     * @return - list of orders or error message
     */
    @RequestMapping("/listOfOrders")
    public ResponseEntity<?> getListOfOrders(String token) {
        try {
            return ResponseEntity.ok(orderService.getListOfOrders(token));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    /**
     * @param token - token of user
     * @param order - order request dto
     * @return - order response dto or error message
     */
    @RequestMapping("/changeStatus")
    public ResponseEntity<?> changeStatus(String token, Order order) {
        try {
            return ResponseEntity.ok(orderService.changeOrderStatus(token, order));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    /**
     * @param token - token of user
     * @param order - order request dto
     * @return - order response dto or error message
     */
    @RequestMapping("/infoAboutOrder")
    public ResponseEntity<?> getInfoAboutOrder(String token, Order order) {
        try {
            return ResponseEntity.ok(orderService.getInfoAboutOrder(token, order));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }
}
