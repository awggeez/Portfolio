package com.kpo.agents;

import com.kpo.constants.Constants;
import com.kpo.util.SuperDish;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.*;

import com.kpo.util.Visitor;
import org.json.JSONObject;

import java.util.Scanner;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VisitorAgent extends Agent {
    private Visitor visitor;
    private Double minutes;  // �����, �� ������� ������������ ����� �������� �����(��. ����� ���������� ���. 9)

    public VisitorAgent(Visitor visitor) {
        this.visitor = visitor;
    }

    @Override
    protected void setup() {
        System.out.println("Enter time in minutes: ");
        // Enter waiting time for visitor
        minutes = new Scanner(System.in).nextDouble();
//         minutes = 0.15;
        visitor = getArguments()[0] instanceof Visitor ? (Visitor) getArguments()[0] : null;
        System.out.printf("Visitor-agent %s is ready.%n", visitor.getName());
        assert visitor != null;
        System.out.println("Hello! Visitor-agent " + visitor.getName() + " is ready.");
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("super-visor", AID.ISLOCALNAME));
//                 System.out.println("Visitor-agent " + visitor.getName() + " is sending message to super-visor");
                msg.setContent(minutes.toString());
                msg.setOntology(Constants.GET_MENU);
                send(msg);
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msgFromSuperVisor = receive();
                if (msgFromSuperVisor != null) {
                    String ontology = msgFromSuperVisor.getOntology();
                    String content = msgFromSuperVisor.getContent();
//                     System.out.println("Visitor-agent " + visitor.getName() + " received message from super-visor");
//                     System.out.println(msgFromSuperVisor.getContent());

                    JSONObject order = new JSONObject();
                    JSONObject dishes = new JSONObject();

                    if (Constants.MENU_SEND_ALL_AVAILABLE_DISHES.equals(ontology)) {
//                         System.out.println("Available dishes for my time (" + visitor.getName() + "): " + content);
//                         System.out.println("What I Want To Buy (" + visitor.getName() + "): " + visitor.getOrderDishes());

                        JSONObject jsonObject = new JSONObject(content);
                        JSONObject dishesJson = jsonObject.getJSONObject("dishes");
                        JSONObject productsJson = jsonObject.getJSONObject("products");
//                         System.out.println("Visitor " + visitor.getName() + " get AvailableDishes from Super-Visor: " + dishJson);
                        for (SuperDish superDish : visitor.getOrderDishes()) {
//                             System.out.println("Try to add " + superDish.getMenuDishId() + " to visitor " + visitor.getName() + " order");
//                             System.out.println("Kringe: " + superDish.getMenuDishId() + " " + dishesJson.has(String.valueOf(superDish.getMenuDishId())));
                            if (dishesJson.has(String.valueOf(superDish.getMenuDishId()))) {
                                JSONObject temp = new JSONObject();
                                temp.put("products", productsJson.get(String.valueOf(superDish.getMenuDishId())));
                                temp.put("amount", "1");
                                String menuDishId = String.valueOf(superDish.getMenuDishId());
                                if (dishes.has(menuDishId)) {
                                    JSONObject temp2 = dishes.getJSONObject(menuDishId);
                                    temp2.put("amount", String.valueOf(Integer.parseInt(temp2.getString("amount")) + 1));
                                    dishes.put(menuDishId, temp2);
                                    continue;
                                }
                                dishes.put(menuDishId, temp);
//                                 System.out.println("Visitor " + visitor.getName() +  ". Dish " + superDish.getMenuDishId() + " is available");
                            } else {
//                                 System.out.println("Visitor " + visitor.getName() +  ". Dish " + superDish.getMenuDishId() + " is not available");
                            }
                        }
                        order.put("user_name", visitor.getName());
                        order.put("dishes", dishes);
//                         System.out.println("Order: " + order);

                        ACLMessage msgToSuperVisor = new ACLMessage(ACLMessage.INFORM);
                        msgToSuperVisor.addReceiver(new AID(Constants.SUPERVISOR_AGENT, AID.ISLOCALNAME));
                        msgToSuperVisor.setContent(order.toString());
                        msgToSuperVisor.setOntology(Constants.ORDER);
                        send(msgToSuperVisor);
                    }
                }
                block();
            }
        });
    }
}
