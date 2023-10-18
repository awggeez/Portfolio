package com.kpo.agents;

import com.kpo.constants.Constants;
import com.kpo.util.Product;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WarehouseAgent extends Agent {
    private Map<Integer, Product> products = new HashMap<>();
    private Map<Integer, Integer> connecterIdWithType = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println("Hello! Warehouse-agent " + getAID().getName() + " is ready.");
        products = (Map<Integer, Product>) getArguments()[0];
        connecterIdWithType = (Map<Integer, Integer>) getArguments()[1];
//         JSONObject allProducts = new JSONObject();
        JSONObject allProducts = new JSONObject();
        JSONObject availableProducts = new JSONObject();
        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
//             allProducts.put(String.valueOf(entry.getKey()), products.get(entry.getKey()).toJSON());
            JSONObject entryValue = products.get(entry.getKey()).toJSON();
            availableProducts.put(entryValue.get("prod_item_type").toString(), entryValue);
        }
        allProducts.put("products", availableProducts);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(); // получаем сообщение от супер визора
                if (msg != null) {
                    // CFP Message received. Process it
                    String title = msg.getContent();
                    String ontology = msg.getOntology();
                    ACLMessage reply = msg.createReply();

                    if (Constants.GET_ALL_GOODS.equals(ontology)) {
                        // Получили сообщение от супервизора, для того чтобы дать ему все доступные продукты
                        JSONObject jsonObject = new JSONObject(title);
                        jsonObject.put("products", availableProducts);
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setOntology(Constants.GET_ALL_GOODS);
                        reply.setContent(jsonObject.toString());
                        myAgent.send(reply);
                    } else if (Constants.PREPARE_GOODS.equals(title)) {
                        // The requested book is NOT available for sale.
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent("not-available");
                        myAgent.send(reply);
                    } else if (Constants.ORDER_RESERVE_PRODUCTS.equals(ontology)) {
                        JSONObject response = new JSONObject(msg.getContent());
                        System.out.println("WARE Response: " + response);
                        JSONObject dishes = (JSONObject) response.get("dishes");
                        JSONObject productsNeedResourses = new JSONObject();
                        for (String dishesId : dishes.keySet()) {
                            JSONObject products = dishes.getJSONObject(dishesId).getJSONObject("products");
                            int amount = dishes.getJSONObject(dishesId).getInt("amount");
                            for (String productId : products.keySet()) {
                                double quantity = products.getDouble(productId) * amount;
                                if (productsNeedResourses.has(productId)) {
                                    productsNeedResourses.put(productId, productsNeedResourses.getDouble(productId) + quantity);
                                } else {
                                    productsNeedResourses.put(productId, quantity);
                                }
                            }
                        }
                        System.out.println("productsNeedResourses: " + productsNeedResourses);
                        JSONObject reservedProductsId = new JSONObject();
                        // Сюда кладём id продуктов которые резервируем
                        reservedProductsId.put("reservedProductsId", new JSONArray());
                        for (String keyId : productsNeedResourses.keySet()) {
                            // Проходимся по всем ресурсам и резервируем их = создаем новых агентов и вычитаем из общего количества
                            try {
                                // TODO
//                                 System.out.println(agentResourseName);
                                double quantity = (double) productsNeedResourses.get(keyId);
                                System.out.println("productInCycle: " + products);
                                int productId = Integer.parseInt(keyId);
                                System.out.println("productId: " + productId);
                                System.out.println("connecterIdWithType: " + connecterIdWithType);
                                int productTypeId = connecterIdWithType.get(productId);
                                System.out.println("keyIntId: " + productId + ", productTypeId: " + productTypeId);

                                String agentResourseName = msg.getSender().getLocalName() + "_" + keyId + "_" + productTypeId;
                                products.get(productTypeId).setQuantity(products.get(productTypeId).getQuantity() - quantity);
                                reservedProductsId.getJSONArray("reservedProductsId").put(agentResourseName);
                                AgentController orderAgentController = getContainerController().createNewAgent(agentResourseName, ProductAgent.class.getName(), new Object[]{quantity});
                                orderAgentController.start();
                            } catch (StaleProxyException e) {
                                System.out.println("Ошибка при создании агента продукта");
                            }
                        }
                        // Отправляем ответ с id резервированных продуктов
                        reply.setPerformative(ACLMessage.PROPOSE);
                        // Уникальная онтология, чтобы ее получил нужный агент(тот, который и создал запрос)
                        reply.setOntology(Constants.ORDER_RESERVE_PRODUCTS + msg.getSender().getLocalName());
                        reply.setContent(reservedProductsId.toString());
                        myAgent.send(reply);
                    }
                }
                block();
            }
        });
    }
}
