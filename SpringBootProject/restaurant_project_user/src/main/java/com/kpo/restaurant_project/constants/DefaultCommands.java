package com.kpo.restaurant_project.constants;

public final class DefaultCommands {

    private DefaultCommands() {
    }

    public static class JwtInfo {
        public static final int ACCESS_MINUTES = 5;
        public static final int REFRESH_MINUTES = 90;
        public static final String ALGORITHM = "HmacSHA512";
        public static final String ALGORITHM_CODE = "HS512";
        public static final String KEY = "umDyGHfmWCdvdfddJtEhn/P52KsyHOPSyHt9zfXKV3F9bP3w7tu5bHr+pC6SnXktif15lK0p7SUNOap7W+11xQ==";
        public static final String ALG = "MD5";
        public static final String TYP = "JWT";
        public static final String AUTH_SERVER = "Auth Server";
    }

    public static class MessagesFromAuth {
        public static final String SUCCESS_AUTH = "Регистрация прошла успешно";
        public static final String NICK_EXIST = "Данный никнейм уже занят";
        public static final String EMAIL_EXIST = "Данный email уже занят";
        public static final String INVALID_PASSWORD = "Неверный пароль";
        public static final String USER_NOT_FOUND = "Пользователь не найден";
        public static final String TOKEN_NOT_FOUND = "Токен не найден";
        public static final String TOKEN_EXPIRED = "Токен просрочен";
    }

    public static class MessagesFromUserService {
        public static final String INVALID_MODE = "Неверный режим";
        public static final String USER_NOT_FOUND = "Пользователь не найден";
        public static final String TOKEN_NOT_PAST = "Токен не прошел валидацию";
        public static final String NO_RIGHTS = "Недостаточно прав";
        public static final String SUCCESS_UPDATE = "OK";
        public static final String ENTER_RIGHT_EMAIL = "Введите правильный email";
        public static final String SUCCESS_ADD = "Пользователь успешно добавлен";
        public static final String USER_WITH_ID = "Пользователь с id = ";
        public static final String SUCCESS_DELETE = " успешно удален";
    }

    public static class MessagesFromValidation {
        public static final String TOKEN_EXPIRES = "Токен просрочен";
        public static final String ERROR_CREATING_TOKEN = "Ошибка создания токена";
        public static final String SUCCESS_CHECK = "OK";
        public static final String SPLITER = "\\.";
    }
}
