package com.kpo.util;

import lombok.*;

// 11 ����� ("vis_ord_dishes")

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuperDish {
    private int orderDishId;
    private int menuDishId;
}


