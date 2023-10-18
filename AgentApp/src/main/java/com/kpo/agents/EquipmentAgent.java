package com.kpo.agents;

import com.kpo.util.Equipment;
import jade.core.Agent;

import lombok.*;

@Data
@AllArgsConstructor
public class EquipmentAgent extends Agent {
    private Equipment equipment;


    @Override
    protected void setup() {
        
    }
}
