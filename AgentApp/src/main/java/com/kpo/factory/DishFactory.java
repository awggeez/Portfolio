package com.kpo.factory;

import com.kpo.util.Dish;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// ����� 1 menu_dishes
@Data
@AllArgsConstructor
public class DishFactory implements DataLoader<Dish> {
    private String pathToMenuFile;

    @Override
    public Map<Integer, Dish> load() {
        Map<Integer, Dish> dishes = new HashMap<>();
        JSONObject json = this.getJSON(pathToMenuFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                JSONObject currentMap = (JSONObject) value;
                if (currentMap.keySet().size() != 4) {
                    System.out.println("Error in menu_dishes.txt file");
                    return null;
                }
                Dish dish;
                try {
                    dish = Dish.builder()
                            .id((Integer) currentMap.get("menu_dish_id"))
                            .price((Double.parseDouble(currentMap.get("menu_dish_price").toString())))
                            .isActive((Boolean) currentMap.get("menu_dish_active"))
                            .cardId((Integer) currentMap.get("menu_dish_card"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in menu_dishes.txt file");
                    return null;
                }
                dishes.put(dish.getId(), dish);
            }
        }
        return dishes;
    }
}
