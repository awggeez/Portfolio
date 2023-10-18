package com.kpo;

import com.kpo.util.*;
import com.kpo.factory.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import lombok.var;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static com.kpo.constants.Constants.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        JSONObject data = getJSON("D:\\KPO\\IDZ1\\src\\main\\java\\com\\kpo\\constants\\files.txt");
        if (data == null) {
            System.out.println("Some troubles with files.txt");
            return;
        }
        DishFactory fabric1 = new DishFactory(data.getString("DishFactory"));
        CardFactory fabric2 = new CardFactory(data.getString("CardFactory"));
        ProductTypeFactory fabric3 = new ProductTypeFactory(data.getString("ProductTypeFactory"));
        ProductFactory fabric4 = new ProductFactory(data.getString("ProductFactory"));
        EquipmentTypeFactory fabric5 = new EquipmentTypeFactory(data.getString("EquipmentTypeFactory"));
        EquipmentFactory fabric6 = new EquipmentFactory(data.getString("EquipmentFactory"));
        CookerFactory fabric7 = new CookerFactory(data.getString("CookerFactory"));
        OperationTypeFactory fabric8 = new OperationTypeFactory(data.getString("OperationTypeFactory"));
        VisitorFactory fabric9 = new VisitorFactory(data.getString("VisitorFactory"));

        Map<Integer, Dish> fabric1Data = fabric1.load();
        Map<Integer, Card> fabric2Data = fabric2.load();
        Map<Integer, ProductType> fabric3Data = fabric3.load();
        Map<Integer, Product> fabric4Data = fabric4.load();
        Map<Integer, EquipmentType> fabric5Data = fabric5.load();
        Map<Integer, Equipment> fabric6Data = fabric6.load();
        Map<Integer, Cooker> fabric7Data = fabric7.load();
        Map<Integer, OperationType> fabric8Data = fabric8.load();
        Map<Integer, Visitor> fabric9Data = fabric9.load();


        if (fabric1Data == null || fabric2Data == null || fabric3Data == null || fabric4Data == null || fabric5Data == null || fabric6Data == null || fabric7Data == null || fabric8Data == null || fabric9Data == null) {
            System.out.println("Error in files");
            return;
        }

        var visitors = fabric9Data;
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
//         profile.setParameter(Profile.GUI, "true");
        ContainerController containerController = runtime.createMainContainer(profile);
        AgentController superVisorAgent, warehouseAgent, visitorAgent, menuAgent;
        try {
            superVisorAgent = containerController.createNewAgent(SUPERVISOR_AGENT, "com.kpo.agents.SuperVisorAgent", null);
            superVisorAgent.start();
            Map<Integer, Product> dataForWarehouseAgent = fabric4Data;
            Map<Integer, Integer> connectorIdWithType = ProductFactory.kostilConnectIdWithType(dataForWarehouseAgent);
            warehouseAgent = containerController.createNewAgent(WAREHOUSE_AGENT, "com.kpo.agents.WarehouseAgent", new Object[]{dataForWarehouseAgent, connectorIdWithType});
            warehouseAgent.start();
            for (Visitor visitor : visitors.values()) {
                visitor.setName(visitor.getName().trim());
                visitorAgent = containerController.createNewAgent(visitor.getName(), "com.kpo.agents.VisitorAgent", new Object[]{visitor});
                visitorAgent.start();
            }
            menuAgent = containerController.createNewAgent(MENU_AGENT, "com.kpo.agents.MenuAgent", new Object[]{fabric1Data, fabric2Data});
            menuAgent.start();
        } catch (StaleProxyException ex) {
            ex.printStackTrace();
        }
    }


    private static JSONObject getJSON(String pathToFile) {
        File file;
        JSONObject data;
        try {
            file = getFile(pathToFile);
            String line = new String(Files.readAllBytes(file.toPath()));
            line = line.replace("\t", "").replace("\n", "");
            data = line.isEmpty() ? new JSONObject() : new JSONObject(line);
        } catch (RuntimeException | IOException e) {
            System.out.printf("Error %s in %s file. Not correct JSON format: %s\n", e, pathToFile, e.getMessage());
            return null;
        }
        return data;
    }

    private static File getFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }
        return file;
    }
}
