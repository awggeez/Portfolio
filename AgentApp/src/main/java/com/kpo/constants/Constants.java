package com.kpo.constants;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String GET_ALL_GOODS = "get-all-goods";
    public static final String PREPARE_GOODS = "prepare-all-goods";
    public static final String GET_MENU = "give-me-menu";

    public static final String MENU_AGENT = "menu";
    public static final String VISITOR_AGENT = "visitor";
    public static final String WAREHOUSE_AGENT = "warehouse";
    public static final String SUPERVISOR_AGENT = "super-visor";
    public static final String GET_AVAILABLE_DISHES = "all-available-dishes";
    public static final String MENU_SEND_ALL_AVAILABLE_DISHES = "all-available-dishes";
    public static final String ORDER = "order-dishes";
    public static final String ORDER_AGENT_START_NAME = "order_agent_";
    public static final String PRODUCT_AGENT_START_NAME = "product_agent_";
    public static final String ORDER_TIME_LEFT = "order-time-left";
    public static final String ORDER_RESERVE_PRODUCTS = "order-reserve-products";
    public static final String GET_CARDS_FROM_MENU = "get-cards-from-menu";
}
