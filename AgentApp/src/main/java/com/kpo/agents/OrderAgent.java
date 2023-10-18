package com.kpo.agents;

import com.kpo.constants.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.kpo.constants.Constants.*;

public class OrderAgent extends Agent {
    private JSONObject request;
    private JSONObject reservedProductAgentsNames;

    @Override
    protected void setup() {
        System.out.println("OrderAgent " + getAID().getName() + " is ready.");
        request = getArguments()[0] != null ? new JSONObject(getArguments()[0].toString()) : new JSONObject();
        reservedProductAgentsNames = new JSONObject();
        System.out.println("Request: " + request);
        JSONObject dishes = request.get("dishes") != null ? (JSONObject) request.get("dishes") : null;
        if (dishes == null) {
            takeDown();
        }
        JSONObject order = new JSONObject();
        order.put("dishes", dishes);
        order.put("user_name", request.get("user_name"));
        System.out.println("Dishes: " + order);

        // Резервация продуктов для блюд
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                if (order == null) {
                    takeDown();
                }
//                 System.out.println("Try to reserve products for dishes: " + order);
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(WAREHOUSE_AGENT, AID.ISLOCALNAME));
                msg.setOntology(ORDER_RESERVE_PRODUCTS);
                msg.setContent(order.toString());
                send(msg);
            }
        });

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage requestToMenu = new ACLMessage(ACLMessage.INFORM);
                requestToMenu.addReceiver(new AID(MENU_AGENT, AID.ISLOCALNAME));
                requestToMenu.setOntology(GET_CARDS_FROM_MENU);
                requestToMenu.setContent(order.toString());
//                System.out.println("Try to send message to MenuAgent: " + order);
                send(requestToMenu);
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                if (order == null) {
                    takeDown();
                }
                ACLMessage response = receive();
                if (response != null) {
                    String ontology = response.getOntology();
                    String content = response.getContent();
//                     System.out.println("OrderAgent " + getAID().getName() + " received message from " + response.getSender().getName() + " with ontology " + ontology + "and content: " + content);
                    if (ORDER_TIME_LEFT.equals(ontology)) {
//                         sendTimeLeft();
                    } else if (GET_CARDS_FROM_MENU.equals(ontology)) {
                        // Получаем карты для данного заказа
                        JSONArray cardsFromMenu = new JSONArray(content);
                        System.out.println("CARDS from menu: " + cardsFromMenu);
                        for (int i = 0; i < cardsFromMenu.length(); i++) {
                            JSONObject card = cardsFromMenu.getJSONObject(i);
                            System.out.println("Card: " + card);

                        }
                    } else if ((Constants.ORDER_RESERVE_PRODUCTS + this.getAgent().getLocalName()).equals(ontology)) {
                        // Получаем карты для данного заказа (потому что в онтологии стоит уникальное название агента)
                        reservedProductAgentsNames = new JSONObject(content);
                        System.out.println("Reserved products(OrderAgent): " + reservedProductAgentsNames);
                    }
                }
                block();
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("OrderAgent " + getAID().getName() + " terminating.");
    }

    private void sendTimeLeft() {
        // сообщение посетителю о приблизительном времени выполнения заказа
        ACLMessage messageToVisitor = new ACLMessage(ACLMessage.INFORM);
        messageToVisitor.addReceiver(new AID(request.get("user_name").toString(), AID.ISLOCALNAME));
        messageToVisitor.setOntology(ORDER_TIME_LEFT);
        messageToVisitor.setContent(getSendTimeLeft());
    }

    private String getSendTimeLeft() {
        // TODO
        return "Ваш заказ будет готов через 10 минут";
    }
}
