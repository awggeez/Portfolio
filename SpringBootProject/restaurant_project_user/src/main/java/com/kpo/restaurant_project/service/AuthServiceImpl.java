package com.kpo.restaurant_project.service;

import com.kpo.restaurant_project.constants.DefaultCommands;
import com.kpo.restaurant_project.domain.models.Role;
import com.kpo.restaurant_project.exceptions.InvalidAuthDataException;
import com.kpo.restaurant_project.dto.JwtDto;
import com.kpo.restaurant_project.domain.models.Session;
import com.kpo.restaurant_project.domain.models.User;
import com.kpo.restaurant_project.domain.repository.SessionRepository;
import com.kpo.restaurant_project.domain.repository.UserRepository;
import com.kpo.restaurant_project.utils.GenerateKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.kpo.restaurant_project.constants.DefaultCommands.JwtInfo.*;
import static com.kpo.restaurant_project.constants.DefaultCommands.MessagesFromAuth.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     *  Method for user registration
     * @param refreshToken - refresh token
     * @return - response with status 200 and new access and refresh tokens if the refresh token is valid
     * @throws InvalidAuthDataException - exception if the refresh token is invalid
     */
    @Override
    public JwtDto getAccessToken(String refreshToken) throws InvalidAuthDataException {
        var token = sessionRepository.findBySessionToken(refreshToken);
        if (token == null) {
            throw new InvalidAuthDataException(HttpStatus.NOT_FOUND, TOKEN_NOT_FOUND);
        }
        if (token.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            sessionRepository.deleteById(token.getId());
            throw new InvalidAuthDataException(HttpStatus.UNAUTHORIZED, TOKEN_EXPIRED);
        }
        return createTokens(token.getUser());
    }

    /**
     * Method for creating access and refresh tokens
     * @param user - user
     * @return - response with status 200 and new access and refresh tokens
     */
    @Override
    public JwtDto createTokens(User user) {
        var encodedInf = JwtHelper.callForEncodedInf(user.getRole(), user.getUsername());
        var signature = JwtHelper.getSignature(encodedInf, KEY, ALGORITHM);

        var token = encodedInf + "." + signature;
        var refreshToken = GenerateKeys.generateKey();

        Session session = sessionRepository.findByUserId(user.getId());

        if (session != null) {
            session.setSessionToken(refreshToken)
                    .setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(REFRESH_MINUTES)));
            sessionRepository.save(session);
        } else {
            session = new Session()
                    .setSessionToken(refreshToken)
                            .setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(REFRESH_MINUTES)));

            user.setSession(session);
            session.setUser(user);
            userRepository.save(user);
        }
        return new JwtDto()
                .setAccessToken(token)
                .setRefreshToken(refreshToken);
    }

    /**
     * Method for user authorization
     * @param user - user
     * @return - response with status 200 and new access and refresh tokens if the user is authorized
     * @throws InvalidAuthDataException - exception if the user is not authorized
     */
    @Override
    public JwtDto login(User user) throws InvalidAuthDataException {
        var currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null) {
            if (currentUser.getPasswordHash().equals(getMD5Hash(user.getPasswordHash()))) {
                return createTokens(currentUser);
            } else {
                throw new InvalidAuthDataException(HttpStatus.CONFLICT, INVALID_PASSWORD);
            }
        } else {
            throw new InvalidAuthDataException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
    }

    /**
     * Method for user registration
     * @param user - user
     * @return - response with status 200 if the user is registered
     * @throws InvalidAuthDataException - exception if the user is not registered
     */
    @Override
    public String signUp(User user) {
        var checker = checkUniqueUsernameAndEmail(user.getUsername(), user.getEmail());
        if (checker == 0) {
            user.setPasswordHash(getMD5Hash(user.getPasswordHash()));
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);
            return SUCCESS_AUTH;
        } else if (checker == 1) {
            return NICK_EXIST;
        } else {
            return EMAIL_EXIST;
        }
    }

    /**
     * Method for user logout
     * @param refreshToken - refresh token
     * @return - response with status 200 if the user is logged out
     * @throws InvalidAuthDataException - exception if the refresh token is invalid
     */
    public static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALG);
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for checking the uniqueness of the username and email
     * @param username - username
     * @param email - email
     * @return - response with status 200 if the username and email are unique
     */
    @Override
    public int checkUniqueUsernameAndEmail(String username, String email) {
        if (userRepository.findByUsername(username) != null) {
            return 1;
        }
        return userRepository.findByEmail(email) == null ? 0 : 2;
    }
}
