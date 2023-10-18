package com.kpo.restaurant_project_order.constants;

public final class DefaultCommands {

    private DefaultCommands() {
    }

    public static class JwtInfo {
        public static final String ALGORITHM = "HmacSHA512";
        public static final String KEY = "umDyGHfmWCdvdfddJtEhn/P52KsyHOPSyHt9zfXKV3F9bP3w7tu5bHr+pC6SnXktif15lK0p7SUNOap7W+11xQ==";
    }

    public static class MessagesFromOrder {
        public static final String DISH_IS_NOT_EXIST = "Блюдо не найдено";
        public static final String DISH_IS_MISSING = "Не хватает товара на складе";
        public static final String WRONG_TOKEN = "Не валидный токен";
        public static final String ORDER_CHANGED = "Статус заказа изменен";
    }

    public static class MessagesFromDish {
        public static final String INVALID_TOKEN = "Не валидный токен";
        public static final String NO_RIGHTS = "Недостаточно прав для добавления блюда";
        public static final String MENU_UPDATED = "Меню обновлено";
        public static final String DISH_ADDED = "Блюдо добавлено";
        public static final String DISH_DELETED = "Блюдо удалено";
        public static final String DISH_NOT_FOUND = "Блюдо не найдено";
    }

    public static class MessagesFromValidation {
        public static final String TOKEN_EXPIRES = "Токен просрочен";
        public static final String TOKEN_IS_NOT_ACTIVATED = "Токен еще не активен";
        public static final String SUCCESS_CHECK = "OK";
        public static final String SPLITER = "\\.";
        public static final String WRONG_TOKEN = "Не валидный токен";
    }
}
