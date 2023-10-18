package com.kpo.factory;

import com.kpo.util.Equipment;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class EquipmentFactory implements DataLoader<Equipment> {

    private String pathToEquipmentFile;
    @Override
    public Map<Integer, Equipment> load() {
        Map<Integer, Equipment> equipments = new HashMap<>();
        JSONObject json = this.getJSON(pathToEquipmentFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                JSONObject currentMap = (JSONObject) value;
                Equipment equipment;
                try {
                    equipment = Equipment.builder()
                            .id((Integer) currentMap.get("equip_id")) // нововвведения в json
                            .type((Integer) currentMap.get("equip_type"))
                            .name((String) currentMap.get("equip_name"))
                            .isActive((Boolean) currentMap.get("equip_active"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in equipment.txt file");
                    return null;
                }
                equipments.put(equipment.getType(), equipment);
            }
        }
        return equipments;
    }
}
