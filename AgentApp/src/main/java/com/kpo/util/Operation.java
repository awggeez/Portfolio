package com.kpo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import org.json.JSONObject;

/*
  *
  * 10. Агент-операция - создается в процессе приготовления блюда
  *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Operation {
    private int id;
    private int process;
    private int card;
    private String started;
    private String ended;
    private int equipId;
    private int cookerId;
    private boolean isActive;
//     private int typeId;

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
        return new JSONObject()
                .put("oper_id", id)
                .put("oper_proc", process)
                .put("oper_card", card)
                .put("oper_started", started)
                .put("oper_ended", ended)
                .put("oper_equip_id", equipId) // нововведения в json
                .put("oper_coocker_id", cookerId)
                .put("oper_active", isActive);
    }
}