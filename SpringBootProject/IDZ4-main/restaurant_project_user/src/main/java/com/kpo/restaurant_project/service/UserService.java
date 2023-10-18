package com.kpo.restaurant_project.service;

import com.kpo.restaurant_project.domain.models.Role;
import com.kpo.restaurant_project.domain.models.User;
import com.kpo.restaurant_project.exceptions.InvalidUserServiceDataException;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void saveUser(User user);

    User getUserById(int id);

    void deleteUserById(int id);


    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User findByEmailAndPasswordHash(String username, String email);

    Object getUserInfo(String accessToken);

    Object changeUserRoleById(String accessToken, Integer id, Role role) throws InvalidUserServiceDataException;
}
