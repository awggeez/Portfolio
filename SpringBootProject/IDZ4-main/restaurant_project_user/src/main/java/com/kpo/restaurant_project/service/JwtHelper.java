package com.kpo.restaurant_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpo.restaurant_project.constants.DefaultCommands;
import com.kpo.restaurant_project.dto.JwtHeader;
import com.kpo.restaurant_project.dto.JwtPayload;
import com.kpo.restaurant_project.domain.models.Role;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import static com.kpo.restaurant_project.constants.DefaultCommands.JwtInfo.*;
import static java.util.Calendar.MINUTE;

public class JwtHelper {

    /**
     * @param info      - info for signature
     * @param secret    - secret key
     * @param algorithm - algorithm for signature
     * @return signature
     */
    public static String getSignature(String info, String secret, String algorithm) {
        var keySpec = new SecretKeySpec(secret.getBytes(), algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(mac.doFinal(info.getBytes()));
    }

    /**
     * @param minutes - minutes to expired
     * @return expired time in milliseconds
     */
    public static Long getExpiredIn(int minutes) {
        var calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(MINUTE, minutes);
        return calendar.getTimeInMillis();
    }

    /**
     * @param role     - role of user
     * @param username - username of user
     * @return encoded JWT
     */
    public static String callForEncodedInf(Role role, String username) {

        var headerDto = new JwtHeader()
                .setAlg(ALGORITHM_CODE)
                .setTyp(TYP);

        var expireIn = getExpiredIn(DefaultCommands.JwtInfo.ACCESS_MINUTES);

        var payloadDto = new JwtPayload()
                .setRole(role)
                .setUsername(username)
                .setIssuedAt(System.currentTimeMillis())
                .setIssuer(AUTH_SERVER)
                .setExpiration(expireIn);

        return encodedJson(headerDto, payloadDto);
    }

    /**
     * @param headerDto  - header of JWT
     * @param payloadDto - payload of JWT
     * @return encoded JWT
     */
    private static String encodedJson(JwtHeader headerDto, JwtPayload payloadDto) {
        var objectMapper = new ObjectMapper();
        String jsonEncoded;
        try {
            jsonEncoded = Encoders.BASE64.encode(objectMapper.writeValueAsString(headerDto).getBytes()) +
                          "." +
                          Encoders.BASE64.encode(objectMapper.writeValueAsString(payloadDto).getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonEncoded;
    }
}
