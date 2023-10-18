package com.kpo.util;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {
    private int id;
    private String name;
    private boolean isFood;
}
