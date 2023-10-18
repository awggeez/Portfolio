package com.kpo.agents;

import com.kpo.constants.Constants;
import com.kpo.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kpo.constants.Constants.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class MenuAgent extends Agent {
    private Menu menu;
    private CardsMenu cardsMenu;

    @Override
    protected void setup() {
        System.out.println("Menu agent started");
        menu = new Menu((Map<Integer, Dish>) getArguments()[0]);
        cardsMenu = new CardsMenu((Map<Integer, Card>) getArguments()[1]);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(); // Получаем сообщение от супер-визора
                if (msg != null) {
                    String ontology = msg.getOntology();
                    String content = msg.getContent();
                    if (GET_AVAILABLE_DISHES.equals(ontology)) {
                        System.out.println("Menu agent received message from super-visor");
//                     System.out.println("Content: " + msg.getContent() + " Ontology: " + msg.getOntology());
                        JSONObject request = new JSONObject(content);
//                         System.out.println("request: " + request);
                        // Получаем список продуктов, которые есть в наличии на складе
                        JSONObject availableProducts = (JSONObject) request.get("products");
                        // Получаем необходимое время для приготовления блюда
                        double needTime = request.getDouble("time");
                        // Получаем список блюд, которые можно приготовить
                        JSONObject result = getSatisfying(needTime, availableProducts);
//                        System.out.println("result of work: " + result);

                        // Заменяем request для отправления обратно
                        request.remove("products");
                        request.put("dishes", result.get("dishes"));
                        request.put("products", result.get("products"));

                        ACLMessage replyToSuperVisor = new ACLMessage(ACLMessage.INFORM);
                        replyToSuperVisor.addReceiver(new AID(SUPERVISOR_AGENT, AID.ISLOCALNAME));
                        replyToSuperVisor.setOntology(Constants.MENU_SEND_ALL_AVAILABLE_DISHES);
                        replyToSuperVisor.setContent(request.toString());
                        send(replyToSuperVisor);
                    } else if (GET_CARDS_FROM_MENU.equals(ontology)) {
                        // Получили запрос на создание карточек блюд по их id
                        System.out.println("Menu agent received message from " + msg.getSender().getLocalName() + " request: " + content);
                        JSONObject request = new JSONObject(content);
                        JSONObject dishes = (JSONObject) request.get("dishes");
                        JSONArray cardsToSend = new JSONArray();

                        Map<Integer, Dish> menuDishes = menu.getDishes();
                        Map<Integer, Card> cards = cardsMenu.getCards();
                        // Перебираем все блюда, которые нужно отправить
                        for (String dishId : dishes.keySet()) {
                            // Получаем карточку блюда по его id
                            Card card = cards.get(menuDishes.get(Integer.parseInt(dishId)).getCardId());
                            // Добавляем карточку в список карточек, которые нужно отправить
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("dish_id", card.getId());
                            jsonObject.put("card", card.toJson());
                            cardsToSend.put(jsonObject);
                        }
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setOntology(GET_CARDS_FROM_MENU);
                        reply.setContent(String.valueOf(cardsToSend));
                        send(reply);
                    }
                }
                block();
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Menu agent finished");
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        System.out.println("Seller-agent " + getAID().getName() + " terminating.");
    }

    // Возвращает меню в формате JSON: {dishes: [{menu_dish_id: 'dish1', menu_dish_price: 100, menu_dish_card: 'card1'}, ...]}

    public JSONObject getDishes() {
        JSONObject dishes = new JSONObject();
        dishes.put("dishes", new JSONArray());
        for (Dish dish : menu.getDishes().values()) {
            JSONObject dishJson = new JSONObject();
            dishJson.put("menu_dish_id", dish.getId());
            dishJson.put("menu_dish_price", dish.getPrice());
            dishJson.put("menu_dish_active", dish.isActive());
            dishJson.put("menu_dish_card", dish.getCardId());
            dishes.getJSONArray("dishes").put(dishJson);
        }
        return dishes;
    }

    private JSONObject getSatisfying(double needTime, JSONObject productsFromWarehouse) {
        JSONObject resultDishes = new JSONObject();
        JSONObject resultProducts = new JSONObject();
        Card currentCard;
        ArrayList<SuperOperation> cardSuperOperation;
        // Проходимся по блюдам из меню
        for (Dish dish : menu.getDishes().values()) {
            JSONObject productThatUsedForThisDish = new JSONObject();
            // Если в меню сказано, что продукт не доступен для продажи - пропускаем
            if (!dish.isActive()) {
                continue;
            }
            boolean canCookThisDish = true;
            // Смотрим, какая карта используется для приготовления этого блюда
            currentCard = cardsMenu.getCards().get(dish.getCardId());
            if (currentCard == null) {
                continue;
            }
            // Получаем список суперопераций для этой карты
            cardSuperOperation = (ArrayList<SuperOperation>) currentCard.getSuperOperations();
            // Проходимся по супероперациям
            for (SuperOperation superOperation : cardSuperOperation) {
                if (!canCookThisDish) {
                    break;
                }
                double timeForCooking = superOperation.getTime();
                // В супероперации есть продукты, с которыми она совершает операцию
                List<Map<String, Number>> productsIdList = superOperation.getProductsId();
                if (timeForCooking > needTime) {
                    canCookThisDish = false;
                    continue;
                }
                // Пройдемся по этим продуктам
                productThatUsedForThisDish = new JSONObject();
                for (Map<String, Number> product : productsIdList) {
                    // Получаем информацию об этом продукте со склада
                    JSONObject productFromWarehouseCharacteristics = (JSONObject) productsFromWarehouse.get(product.get("prod_type").toString());
                    double quantityAvailableOnWarehouse = productFromWarehouseCharacteristics.getDouble("prod_item_quantity");  // Какое количество продукта доступно на складе
                    // Если на складе нет нужного количества продукта, то не можем приготовить блюдо и переходим к следующему блюду
                    if (quantityAvailableOnWarehouse < product.get("prod_quantity").doubleValue()) {
                        canCookThisDish = false;
                        break;
                    }
                    productThatUsedForThisDish.put(product.get("prod_type").toString(), product.get("prod_quantity").doubleValue());
                }
            }
            if (!canCookThisDish) {
                break;
            }
            // Если мы дошли до этого места, то значит, что блюдо можно приготовить
            resultDishes.put(String.valueOf(dish.getId()), dish.toJSON());
            resultProducts.put(String.valueOf(dish.getId()), productThatUsedForThisDish);
        }
        JSONObject result = new JSONObject();
        result.put("dishes", resultDishes);
        result.put("products", resultProducts);
        return result;
    }

}