package com.kpo.factory;

import com.kpo.util.SuperDish;
import com.kpo.util.Visitor;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class VisitorFactory implements DataLoader<Visitor> {
    private String pathToVisitorsFile;

    @Override
    public Map<Integer, Visitor> load() {
        int newUserID = 0;
        Map<Integer, Visitor> visitors = new HashMap<>();
        JSONObject json = this.getJSON(pathToVisitorsFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            // Проходимся по всем ключам("главные" ключи) в самом json(на данный момент это только dish_cards)
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                // Проходимся повсем значениям "главных" ключей
                JSONObject currentMap = (JSONObject) value;
                // Создаем карточку посетителя
                Visitor visitor;
                try {
                    visitor = Visitor.builder()
                            .id(newUserID++)
                            .name(((String) currentMap.get("vis_name")))
                            .orderStarted((String) currentMap.get("vis_ord_started"))
                            .orderEnded((String) (currentMap.get("vis_ord_ended")))
                            .orderTotal((Integer) currentMap.get("vis_ord_total"))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in visitors.txt file");
                    return null;
                }

                List<SuperDish> superDishes = new ArrayList<>();
                // У визитора есть список "суперблюд" - сложная фигня
                JSONArray dishesArray = currentMap.getJSONArray("vis_ord_dishes");
                for (Object dish : dishesArray) {
                    // Проходимся по всем свойствам суперблюд
                    try {
                        JSONObject superDishJSON = (JSONObject) dish;
                        SuperDish superDish = SuperDish.builder()
                                .orderDishId((Integer) superDishJSON.get("ord_dish_id"))
                                .menuDishId((Integer) superDishJSON.get("menu_dish"))
                                .build();
                        superDishes.add(superDish);  // Добавляем супероперацию в список суперопераций
                    } catch (Exception e) {
                        // Ошибка при чтении
                    }
                }
                visitor.setOrderDishes(superDishes);

                visitors.put(visitor.getId(), visitor);
            }
        }
        return visitors;
    }
}
