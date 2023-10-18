package com.kpo.restaurant_project_order.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpo.restaurant_project_order.dto.JwtHeader;
import com.kpo.restaurant_project_order.dto.JwtPayload;
import com.kpo.restaurant_project_order.exceptions.InvalidOperationException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.kpo.restaurant_project_order.constants.DefaultCommands.JwtInfo.ALGORITHM;
import static com.kpo.restaurant_project_order.constants.DefaultCommands.JwtInfo.KEY;
import static com.kpo.restaurant_project_order.constants.DefaultCommands.MessagesFromValidation.*;

public class Validator {
    public static String header, payload, hashed, accessToken;

    public static JwtPayload getPayloadFromToken() throws InvalidOperationException {
        if (!Validator.tokenIsValid()) {
            throw new InvalidOperationException(HttpStatus.UNAUTHORIZED, WRONG_TOKEN);
        }
        JwtPayload payloadDto = Validator.getPayload();
        if (payloadDto == null) {
            throw new InvalidOperationException(HttpStatus.UNAUTHORIZED, WRONG_TOKEN);
        }
        String payloadCheck = Validator.checkPayload(payloadDto);
        if (!SUCCESS_CHECK.equals(payloadCheck)) {
            throw new InvalidOperationException(HttpStatus.UNAUTHORIZED, payloadCheck);
        }
        return payloadDto;
    }


    private static boolean tokenIsValid() {
        String[] parts = accessToken.split(SPLITER);
        if (parts.length != 3) {
            return false;
        }
        try {
            header = new String(Base64.decodeBase64(parts[0]));
            payload = new String(Base64.decodeBase64(parts[1]));
            hashed = getSignature(parts[0] + "." + parts[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!hashed.equals(parts[2])) {
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readValue(header, JwtHeader.class);
            objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static JwtPayload getPayload() {
        String[] parts = accessToken.split(SPLITER);
        String payload = new String(Base64.decodeBase64(parts[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String checkPayload(JwtPayload payloadDto) {
        Long notBefore = payloadDto.getIssuedAt();
        Long expiredIn = payloadDto.getExpiration();
        Long current = System.currentTimeMillis();
        if (expiredIn < current) {
            return TOKEN_EXPIRES;
        } else if (current < notBefore) {
            return TOKEN_IS_NOT_ACTIVATED;
        }
        return SUCCESS_CHECK;
    }
    private static String getSignature(String data) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Mac mac;
        try {
            mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return java.util.Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
    }
}
