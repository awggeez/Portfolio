package com.kpo.factory;

import com.kpo.util.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ProductFactory implements DataLoader<Product> {

    private String pathToProductFile;
    @Override
    public Map<Integer, Product> load() {
        Map<Integer, Product> products = new HashMap<>();
        JSONObject json = this.getJSON(pathToProductFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                JSONObject currentMap = (JSONObject) value;
                Product product;
                try {
                    product = Product.builder()
                            .id((Integer) currentMap.get("prod_item_id"))
                            .type((Integer) (currentMap.get("prod_item_type")))
                            .name((String) currentMap.get("prod_item_name"))
                            .company((String) currentMap.get("prod_item_company"))
                            .unit((String) currentMap.get("prod_item_unit"))
                            .quantity((Double.parseDouble(currentMap.get("prod_item_quantity").toString())))
                            .cost((Double.parseDouble(currentMap.get("prod_item_cost").toString())))
                            .delivered((String) currentMap.get("prod_item_delivered"))
                            .validUntil((String) currentMap.get("prod_item_valid_until"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in product.txt file");
                    return null;
                }
                products.put(product.getId(), product);
            }
        }
        return products;
    }

    public static Map<Integer, Integer> kostilConnectIdWithType(Map<Integer, Product> products) {
        // Пришлось создать такой метод, потому что только ближе к концу обнаружили, что вместо ProductId используется ProductTypeId(см. файл WarehouseAgent)
        Map<Integer, Integer> map = new HashMap<>();
        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
            map.put(entry.getValue().getType(), entry.getKey());
        }
        return map;
    }
}
