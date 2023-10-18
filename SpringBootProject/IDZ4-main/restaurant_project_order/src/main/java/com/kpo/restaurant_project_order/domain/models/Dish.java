package com.kpo.restaurant_project_order.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "dish")
@Data
@Accessors(chain = true)
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Integer quantity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dish")
    private List<OrderDish> orderDishes;
}
