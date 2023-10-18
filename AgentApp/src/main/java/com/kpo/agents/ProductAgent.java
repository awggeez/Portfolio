package com.kpo.agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.JSONObject;

public class ProductAgent extends Agent {
    double quantity;
    @Override
    protected void setup() {
        System.out.println("ProductAgent " + getAID().getName() + " is ready.");
        quantity = getArguments()[0] != null ? Double.parseDouble(getArguments()[0].toString()) : 0;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String ontology = msg.getOntology();
                    String content = msg.getContent();

                   
                    // TODO - резервирование, отмена резервирования
                } else {
                    block();
                }
            }
        });
    }
}