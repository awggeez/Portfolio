package com.kpo.agents;

import com.kpo.util.Cooker;
import jade.core.Agent;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CookerAgent extends Agent {
    private Cooker cooker;
}