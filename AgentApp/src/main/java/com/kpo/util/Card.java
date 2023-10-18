package com.kpo.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    private int id;
    private String dishName;
    private String description;
    private double time;
//     private int equipmentId; - очень вовремя поменяли json, спасибо блин
    private List<SuperOperation> superOperations;


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("card_id", id);
        jsonObject.put("dish_name", dishName);
        jsonObject.put("card_descr", description);
        jsonObject.put("card_time", time);
        jsonObject.put("operations", superOperations);
        JSONArray jsonArray = new JSONArray();
        for (SuperOperation superOperation : superOperations) {
            jsonArray.put(superOperation.toJson());
        }
        return jsonObject;
    }

}