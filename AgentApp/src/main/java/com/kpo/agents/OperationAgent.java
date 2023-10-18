package com.kpo.agents;

import com.kpo.util.Operation;
import jade.core.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OperationAgent extends Agent {
    private Operation operation;
    @Override
    protected void setup() {
        System.out.println("OperationAgent " + getAID().getName() + " is ready.");
        System.out.println("Operation: " + operation);
    }
}
