package com.kpo.agents;

import com.kpo.constants.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.kpo.constants.Constants.*;

// Управляющий агент
public class SuperVisorAgent extends Agent {
    Map<String, Double> timesForVisitors = new HashMap<>();

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
//                     System.out.println("Super-visor received message from " + msg.getSender().getName() + " with content " + msg.getContent());
//                    JOptionPane.showMessageDialog(null, msg.getContent());
//                     System.out.println();
                    String messageOntology = msg.getOntology();
                    String visitorName;
                    switch (messageOntology) {
                        case Constants.GET_MENU:
                            visitorName = msg.getSender().getLocalName(); // LocalName == vistor.getName() по условию
                            timesForVisitors.put(visitorName, Double.parseDouble(msg.getContent()));

                            double time = timesForVisitors.get(visitorName);
                            timesForVisitors.put(visitorName, time);

                            ACLMessage msgToWarehouse = new ACLMessage(ACLMessage.INFORM);
                            msgToWarehouse.addReceiver(new AID(WAREHOUSE_AGENT, AID.ISLOCALNAME));
                            msgToWarehouse.setOntology(Constants.GET_ALL_GOODS);
                            msgToWarehouse.setContent(new JSONObject().put("user_name", visitorName).put("time", time).toString());
                            send(msgToWarehouse);
                            break;
                        case Constants.GET_ALL_GOODS:
                            // Нам ответил склад на запрос о выдаче всех доступных ресурсов
                            JSONObject messageFromWarehouse = new JSONObject(msg.getContent());
                            // menu.put("time", timesForVisitors.get())
                            ACLMessage msgToMenuAgent = new ACLMessage(ACLMessage.INFORM);
                            msgToMenuAgent.addReceiver(new AID(MENU_AGENT, AID.ISLOCALNAME));
                            msgToMenuAgent.setContent(messageFromWarehouse.toString());
                            msgToMenuAgent.setOntology(Constants.GET_AVAILABLE_DISHES);
//                             System.out.println("Super-visor sent message to menu with content " + messageFromWarehouse);
                            send(msgToMenuAgent);
                            break;
                        case Constants.MENU_SEND_ALL_AVAILABLE_DISHES:
                            // Нам ответил меню на запрос о выдаче всех доступных блюд для пользователя\
                            JSONObject response = new JSONObject(msg.getContent());
                            visitorName = response.getString("user_name");

                            response.remove("user_name");
                            response.remove("time");

//                             System.out.println("Super-visor try to send message to " + visitorName + " with content " + response);

                            ACLMessage replyToVisitor = new ACLMessage(ACLMessage.INFORM);
                            replyToVisitor.addReceiver(new AID(visitorName, AID.ISLOCALNAME));
                            replyToVisitor.setOntology(Constants.MENU_SEND_ALL_AVAILABLE_DISHES);
                            replyToVisitor.setContent(response.toString());
                            send(replyToVisitor);
                            break;
                        case Constants.ORDER:
                            // Пользователь сделал заказ. Время его выполнять
                            JSONObject order = new JSONObject(msg.getContent());
                            visitorName = order.getString("user_name");

                            System.out.println("Super-visor received order from " + visitorName + " with content " + order);
                            try {
                                AgentController orderAgentController = getContainerController().createNewAgent(ORDER_AGENT_START_NAME + visitorName, OrderAgent.class.getName(), new Object[]{order});
                                orderAgentController.start();
                            } catch (StaleProxyException e) {
                                System.out.println("Ошибка при создании агента-заказа");
                            }
//                             ACLMessage msgToWarehouseAgent = new ACLMessage(ACLMessage.INFORM);
//                             msgToWarehouseAgent.addReceiver(new AID(WAREHOUSE_AGENT, AID.ISLOCALNAME));
//                             msgToWarehouseAgent.setOntology(Constants.ORDER);
//                             msgToWarehouseAgent.setContent(order.toString());
//                             send(msgToWarehouseAgent);
                            break;
                        default:
                            System.out.println("Unknown ontology :(");
                    }

//                    if (Constants.GET_MENU.equals(messageOntology)) {
//                        // Нам написал пользователь с просьбой дать ему меню со временем
//
//                    } else if (Constants.GET_ALL_GOODS.equals(messageOntology)) {
//
//                    } else if (Constants.MENU_SEND_ALL_AVAILABLE_DISHES.equals(messageOntology)) {
//
//                    }
                }
                block();
            }
        });

    }

    /**
     * Inner class OfferRequestsServer.
     * This is the behaviour used by Book-seller agents to serve incoming requests
     * for offer from buyer agents.
     * If the requested book is in the local catalogue the seller agent replies
     * with a PROPOSE message specifying the price. Otherwise a REFUSE message is
     * sent back.
     */
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // TODO: Пока что без онтологий - на чистом title

                // CFP Message received. Process it
                String request = msg.getContent();
                ACLMessage reply = msg.createReply();

//                 JSONObject json = new JSONObject(request);
                JSONArray array = new JSONArray(request);
//                 System.out.println(array);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
//                     System.out.println(object);
                    int orderDishId = object.getInt("ord_dish_id");
                    int menuDishId = object.getInt("menu_dish");

                }
            }
            block();
        }
    }  // End of inner class OfferRequestsServer


    //Возвращает JSONOnbject с доступными блюдами в формате  [{dish_id: 1, time: 10.5}, {dish_id: 2, time: 20}, ...]

    private JSONObject getAvailableDishes(double needTime) {
        // Отправить запрос агенту MenuAgent и получить ответ
        // Проверить, что время приготовления блюда не превышает needTime
        // Если превышает, то удалить из списка
        // Если не превышает, то добавить в список
        // Вернуть список (формат {"availableDishes": [{..}, {..}, ...]})

        JSONObject menu = getAllDishesFromMenu();
//         JSONObject availableResourses = getResoursesFromWarehouse();
        JSONArray availableDishes = new JSONArray();

        // TODO - проверка доступных ресурсов на соответствие времени приготовления блюда
        for (int i = 0; i < menu.length(); i++) {
            JSONObject dish = menu.getJSONObject(String.valueOf(i));
            int dishId = dish.getInt("dish_id");
            double time = dish.getDouble("time");
            if (time > needTime) {
                continue;
            }
            availableDishes.put(dish);
        }
        return new JSONObject().put("dishes", availableDishes);
    }

    private JSONObject getAllDishesFromMenu() {
        // TODO - отправка запроса агенту MenuAgent и получение ответа со всем меню
        return null;
    }

    private JSONObject getResoursesFromWarehouse() {
        // TODO - отправка запроса агенту MenuAgent и получение ответа со всеми доступными ресурсами
        return null;
    }

    private boolean checkResourses(JSONObject dish, JSONObject resourses) {
        // TODO - проверка доступных ресурсов на соответствие блюду

        return false;
    }


}