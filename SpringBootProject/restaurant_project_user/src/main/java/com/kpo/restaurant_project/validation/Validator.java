package com.kpo.restaurant_project.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpo.restaurant_project.constants.DefaultCommands;
import com.kpo.restaurant_project.dto.JwtDto;
import com.kpo.restaurant_project.dto.JwtPayload;
import com.kpo.restaurant_project.service.JwtHelper;
import org.apache.tomcat.util.codec.binary.Base64;

import static com.kpo.restaurant_project.constants.DefaultCommands.JwtInfo.*;
import static com.kpo.restaurant_project.constants.DefaultCommands.MessagesFromValidation.*;

public class Validator {
    public static String header, payload, hashed, accessToken;

    Validator(String accessToken) {
        Validator.accessToken = accessToken;
    }

    public static boolean checkValidation() {
        String[] parts = accessToken.split(SPLITER);
        if (parts.length != 3) {
            return false;
        }
        try {
            header = new String(Base64.decodeBase64(parts[0]));
            payload = new String(Base64.decodeBase64(parts[1]));
            hashed = JwtHelper.getSignature(parts[0] + "." + parts[1], KEY, ALGORITHM);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!hashed.equals(parts[2])) {
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readValue(header, JwtDto.class);
            objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static JwtPayload getPayload(String accessToken) {
        String payload = new String(Base64.decodeBase64(accessToken.split(SPLITER)[1]));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkPayload(JwtPayload payloadDto) {
        Long current = System.currentTimeMillis();
        if (payloadDto.getExpiration() < current) {
            return TOKEN_EXPIRES;
        } else if (current < payloadDto.getIssuedAt()) {
            return ERROR_CREATING_TOKEN;
        }
        return SUCCESS_CHECK;
    }
}
