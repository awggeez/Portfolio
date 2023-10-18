package com.kpo.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuperOperation {
    private int type;
    private int equipType; // изменение в json
    private double time;
    private int asyncPoint;
    List<Map<String, Number>> productsId; // [{"prod_type": ..., "prod_quiantity": ...}, ...]
    /*
    List<Product> products = new ArrayList<>();
                    JSONArray productsArray = currentMap.getJSONArray("products");
    */

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oper_type", type);
        jsonObject.put("equip_type", equipType);
        jsonObject.put("oper_time", time);
        jsonObject.put("oper_async_point", asyncPoint);
        jsonObject.put("oper_products", new JSONArray());
        for (Map<String, Number> product : productsId) {
            JSONObject productJson = new JSONObject();
            productJson.put("prod_type", product.get("prod_type"));
            productJson.put("prod_quiantity", product.get("prod_quiantity"));
            jsonObject.append("oper_products", productJson);
        }
        return jsonObject;
    }
}
