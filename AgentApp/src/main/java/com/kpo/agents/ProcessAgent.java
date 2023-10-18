package com.kpo.agents;

/*
 *
 * 9. Операция - приготовление блюда(состоит из процессов (пункт 10))
 *
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import lombok.*;

import java.util.List;

import org.json.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessAgent extends Agent {
    private int id;
    private int orderDish;
    private String started;
    private String ended;
    private boolean isActive;
    private List<Integer> operationsId; // TODO заменим на int - id операций

    @SneakyThrows
    public String toString() {
        // return "{\n" +
        // "\"proc_id\": " + id +
        // ",\n\"ord_dish\": " + orderDish +
        // ",\n\"proc_started\": \"" + started + "\"" +
        // "\"\n,\"proc_ended\": \"" + ended + "\"" +
        // ",\n\"proc_active\"=" + isActive +
        // ",\nprocessOperations=" + processOperations +
        // '}';
        ObjectMapper mapper = new ObjectMapper();
        Object jsonObject = mapper.readValue(this.toJson().toString(), Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject()
                .put("proc_id", id)
                .put("ord_dish", orderDish)
                .put("proc_started", started)
                .put("proc_ended", ended)
                .put("proc_active", isActive);
        // В формат
        // "proc_operations": [{
        //      "proc_oper": 82325
        // }]

        for (int i = 0; i < operationsId.size(); i++) {
            json.append("proc_operations", new JSONObject().put("proc_oper", operationsId.get(i)));
        }
        return json;
    }
}
