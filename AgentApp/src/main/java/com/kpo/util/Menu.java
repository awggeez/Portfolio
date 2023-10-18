package com.kpo.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Menu {
    private Map<Integer, Dish> dishes;
}