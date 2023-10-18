package com.kpo.restaurant_project.service;

import com.kpo.restaurant_project.exceptions.InvalidAuthDataException;
import com.kpo.restaurant_project.dto.JwtDto;
import com.kpo.restaurant_project.domain.models.User;

public interface AuthService {
    JwtDto getAccessToken(String refreshToken) throws InvalidAuthDataException;

    JwtDto createTokens(User user);

    JwtDto login(User user) throws InvalidAuthDataException;

    String signUp(User user) throws InvalidAuthDataException;

    int checkUniqueUsernameAndEmail(String username, String email);
}
