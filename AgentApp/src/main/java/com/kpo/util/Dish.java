package com.kpo.util;

import lombok.*;
import org.json.JSONObject;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dish {
    private int id;
    private double price;
    private boolean isActive;
    private int cardId;

    public JSONObject toJSON() {
        return new JSONObject()
                .put("menu_dish_id", id)
                .put("menu_dish_card", cardId)
                .put("menu_dish_price", price)
                .put("menu_dish_active", isActive);
    }
}
