package com.kpo.restaurant_project.controller;

import com.kpo.restaurant_project.domain.models.User;
import com.kpo.restaurant_project.exceptions.InvalidAuthDataException;
import com.kpo.restaurant_project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     *  Register in the system
     * @param user - user data to register in the system
     * @return - response with status 200 and user data if the user was successfully registered
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(authService.signUp(user));
        } catch (InvalidAuthDataException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }

    /**
     *  Login in the system
     * @param user - user data to login in the system
     * @return - response with status 200 and user data if the user was successfully logged in
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(authService.login(user));
        } catch (InvalidAuthDataException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }

    /**
     *  Get new access and refresh tokens
     * @param refreshToken - refresh token to get new access and refresh tokens
     * @return - response with status 200 and new access and refresh tokens if the refresh token is valid
     */
    @PostMapping("/token")
    public ResponseEntity<?> getNewAccessAndRefreshToken(@RequestBody String refreshToken) {
        try {
            return ResponseEntity.ok(authService.getAccessToken(refreshToken));
        } catch (InvalidAuthDataException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }
}