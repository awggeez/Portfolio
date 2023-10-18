package com.kpo.restaurant_project.service;

import com.kpo.restaurant_project.constants.DefaultCommands;
import com.kpo.restaurant_project.exceptions.InvalidUserServiceDataException;
import com.kpo.restaurant_project.dto.JwtPayload;
import com.kpo.restaurant_project.domain.models.Role;
import com.kpo.restaurant_project.domain.models.User;
import com.kpo.restaurant_project.domain.repository.UserRepository;
import com.kpo.restaurant_project.validation.Validator;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kpo.restaurant_project.constants.DefaultCommands.MessagesFromUserService.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * @param user - user
     * @throws ConstraintViolationException - exception
     */
    @Override
    public void saveUser(User user) throws ConstraintViolationException {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        userRepository.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * @param id - id of user
     */
    @Override
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    /**
     * @param username - username
     * @return - user
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * @param email - email
     * @return - user
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * @param username - username
     * @param email    - email
     * @return - user
     */
    @Override
    public User findByEmailAndPasswordHash(String username, String email) {
        return userRepository.findByEmailAndPasswordHash(username, email);
    }

    /**
     * @param accessToken - token
     * @return - user info
     */
    @Override
    public Object getUserInfo(String accessToken) {
        return null;
    }

    /**
     * @param accessToken - token
     * @param id          - user id
     * @param currentRole - current role
     * @return - success message
     * @throws InvalidUserServiceDataException - exception
     */
    @Override
    public Object changeUserRoleById(String accessToken, Integer id, Role currentRole) throws InvalidUserServiceDataException {
        String mode = currentRole.name();
        Validator.accessToken = accessToken;
        if (!Validator.checkValidation()) {
            throw new InvalidUserServiceDataException(HttpStatus.FORBIDDEN, TOKEN_NOT_PAST);
        }
        JwtPayload payloadDto = Validator.getPayload(accessToken);
        if (payloadDto == null) {
            throw new InvalidUserServiceDataException(HttpStatus.FORBIDDEN, TOKEN_NOT_PAST);
        }
        String payloadCheck = Validator.checkPayload(payloadDto);
        if (!SUCCESS_UPDATE.equals(payloadCheck)) {
            throw new InvalidUserServiceDataException(HttpStatus.UNAUTHORIZED, payloadCheck);
        }

        if (!Objects.equals(Role.MANAGER, payloadDto.getRole())) {
            throw new InvalidUserServiceDataException(HttpStatus.BAD_REQUEST, NO_RIGHTS);
        }
        Role role;
        try {
            role = Role.valueOf(mode);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserServiceDataException(HttpStatus.CONFLICT, INVALID_MODE);
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new InvalidUserServiceDataException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setRole(role)
                .setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
        return SUCCESS_UPDATE;
    }
}
