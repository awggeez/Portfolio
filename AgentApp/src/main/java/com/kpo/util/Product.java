package com.kpo.util;

import lombok.*;
import org.json.JSONObject;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    // Входит в "справочник типов продуктов / расходников"
    private int id;
    private int type;
    private String name;
    private String company;
    private String unit;
    private double quantity;
    private double cost;
    private String delivered;
    private String validUntil;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("prod_item_id", id);
        json.put("prod_item_type", type);
        json.put("prod_item_name", name);
        json.put("prod_item_company", company);
        json.put("prod_item_unit", unit);
        json.put("prod_item_quantity", quantity);
        json.put("prod_item_cost", cost);
        json.put("prod_item_delivered", delivered);
        json.put("prod_item_valid_until", validUntil);
        return json;
    }
}