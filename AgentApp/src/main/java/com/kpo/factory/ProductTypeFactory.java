package com.kpo.factory;

import com.kpo.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Пункт 3 product_types
@Data
@AllArgsConstructor
public class ProductTypeFactory implements DataLoader<ProductType> {
    private String pathToProductFile;

    @Override
    public Map<Integer, ProductType> load() {
        Map<Integer, ProductType> dishes = new HashMap<>();
        JSONObject json = this.getJSON(pathToProductFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                JSONObject currentMap = (JSONObject) value;
                ProductType dish;
                try {
                    dish = ProductType.builder()
                            .id((Integer) currentMap.get("prod_type_id"))
                            .name((String) currentMap.get("prod_type_name"))
                            .isFood((Boolean) currentMap.get("prod_is_food"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in product_types.txt file");
                    return null;
                }
                dishes.put(dish.getId(), dish);
            }
        }
        return dishes;
    }
}
