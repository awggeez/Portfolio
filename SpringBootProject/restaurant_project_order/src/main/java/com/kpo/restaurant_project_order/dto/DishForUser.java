package com.kpo.restaurant_project_order.dto;

import com.kpo.restaurant_project_order.domain.models.Dish;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class DishForUser extends Dish {
    private String name;
    private String description;
    private Double price;
    private Boolean exist;
}
