package com.kpo.factory;

import com.kpo.util.Card;
import com.kpo.util.SuperOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Пункт 2 dish_cards
@Data
@AllArgsConstructor
public class CardFactory implements DataLoader<Card> {
    private String pathToCardsFile;

    @Override
    public Map<Integer, Card> load() {
        Map<Integer, Card> cards = new HashMap<>();
        JSONObject json = this.getJSON(pathToCardsFile);
        if (json == null) {
            return null;
        }
        for (String key : json.keySet()) {
            // Проходимся по всем ключам("главные" ключи) в самом json(на данный момент это только dish_cards)
            JSONArray array = json.getJSONArray(key);
            for (Object value : array) {
                // Проходимся повсем значениям "главных" ключей
                JSONObject currentMap = (JSONObject) value;
                // Создаем карточку
                Card card;
                try {
                    card = Card.builder()
                            .id((Integer) currentMap.get("card_id"))
                            .dishName(((String) currentMap.get("dish_name")))
                            .description((String) currentMap.get("card_descr"))
                            .time((Double.parseDouble(currentMap.get("card_time").toString())))
                            .build();
                } catch (Exception e) {
                    System.out.println("Error in dish_cards.txt file");
                    return null;
                }
//                         .equipmentId((Integer) currentMap.get("equip_type")) // спасибо, что поменяли json...

                List<SuperOperation> superOperations = new ArrayList<>();
                // У карточки есть "супероперации" - сложная фигня
                JSONArray operationsArray = currentMap.getJSONArray("operations");
                for (Object operation : operationsArray) {
                    // Проходимся по всем свойствам суперопераций
                    try {
                        JSONObject superOperationJSON = (JSONObject) operation;
                        SuperOperation superOperation = SuperOperation.builder()
                                .type((Integer) superOperationJSON.get("oper_type"))
                                .equipType((Integer) superOperationJSON.get("equip_type"))  // нововведения в json
                                .time((Double.parseDouble(superOperationJSON.get("oper_time").toString())))
                                .asyncPoint((Integer) superOperationJSON.get("oper_async_point"))
                                .build();

                        // У "супероперации" есть продукты, с которыми она работает. Хранит в себе пока id этих продуктов и их количество
                        List<Map<String, Number>> operProducts = new ArrayList<>();
                        JSONArray productsArray = superOperationJSON.getJSONArray("oper_products");
                        for (Object product : productsArray) {

                            // Проходимся по всем продуктам, которые задействованы в супероперации
                            JSONObject currentProduct = (JSONObject) product;
                            Map<String, Number> productMap = new HashMap<>();
                            productMap.put("prod_type", (Number) currentProduct.get("prod_type"));
                            productMap.put("prod_quantity", (Number) currentProduct.get("prod_quantity"));
                            operProducts.add(productMap); // Добавляем продукт в список продуктов
                        }
                        superOperation.setProductsId(operProducts);  // Добавляем список продуктов в супероперацию
                        superOperations.add(superOperation);  // Добавляем супероперацию в список суперопераций
                    } catch (Exception e) {
                        // Ошибка при чтении
                    }
                }
                card.setSuperOperations(superOperations);

                cards.put(card.getId(), card);
            }
        }
        return cards;
    }
}
