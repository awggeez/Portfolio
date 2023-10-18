package com.kpo.util;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Visitor {
    private int id;  // TODO - ждём ответа по поводу того, является ли name уникальным для каждого посетителя
    private String name;
    private String orderStarted;
    private String orderEnded;
    private int orderTotal;
//     private int orderNumber; // связанно с агентом-процесса приготовления блюда
    private List<SuperDish> orderDishes;
}