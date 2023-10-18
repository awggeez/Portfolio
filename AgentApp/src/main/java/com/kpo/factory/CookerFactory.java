package com.kpo.factory;
import com.kpo.util.Cooker;
import lombok.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// ����� 7 cookers
@Data
@AllArgsConstructor
public class CookerFactory implements DataLoader<Cooker> {
    private String pathToCookersFile;

    @Override
    public Map<Integer, Cooker> load() {
        Map<Integer, Cooker> cookers = new HashMap<>();
        JSONObject json = this.getJSON(pathToCookersFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                JSONObject currentMap = (JSONObject) value;
                Cooker cooker;
                try {
                    cooker = Cooker.builder()
                            .id((Integer) currentMap.get("cook_id"))
                            .name((String) currentMap.get("cook_name"))
                            .isActive((Boolean) currentMap.get("cook_active"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in cookers.txt file");
                    return null;
                }
                cookers.put(cooker.getId(), cooker);
            }
        }
        return cookers;
    }

}
