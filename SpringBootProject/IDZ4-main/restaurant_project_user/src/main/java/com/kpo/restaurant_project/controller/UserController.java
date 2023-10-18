package com.kpo.restaurant_project.controller;

import com.kpo.restaurant_project.constants.DefaultCommands;
import com.kpo.restaurant_project.domain.models.User;
import com.kpo.restaurant_project.exceptions.InvalidUserServiceDataException;
import com.kpo.restaurant_project.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kpo.restaurant_project.constants.DefaultCommands.MessagesFromUserService.*;

/*
 * Для дополнительной работы с данными пользователя
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *  Add user
     * @param user - данные пользователя
     * @return - сообщение об успешном добавлении
     */
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        try {
            userService.saveUser(user);
        } catch (ConstraintViolationException e) {
            return ENTER_RIGHT_EMAIL;
        }
        return SUCCESS_ADD;
    }

    /**
     *  Update user
     * @param employee - данные пользователя
     * @return - сообщение об успешном обновлении
     */
    @PutMapping("/employees")
    public User updateEmployee(@RequestBody User employee) {
        userService.saveUser(employee);
        return employee;
    }

    /**
     *  Delete user
     * @param id - id пользователя
     * @return - сообщение об успешном удалении
     */
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return USER_WITH_ID + id + SUCCESS_DELETE;
    }

    /**
     *  Show all users
     * @return - список всех пользователей
     */
    @GetMapping("/all")
    public Iterable<User> showAllUsers() {
        return userService.getAllUsers();
    }

    /**
     *  Show user by id
     * @param id - id пользователя
     * @return - данные пользователя
     */

    @GetMapping("/id/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /**
     * @param accessToken - токен доступа
     * @return - данные пользователя
     */
    @GetMapping("/get_info")
    public ResponseEntity<?> checkAccess(String accessToken) {
        try {
            return ResponseEntity.ok(userService.getUserInfo(accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * @param accessToken - токен доступа
     * @param user - данные пользователя
     * @return - данные пользователя
     */
    @PostMapping("/change_role")
    public ResponseEntity<?> changeRoleById(String accessToken, User user) throws InvalidUserServiceDataException {
        try {
            return ResponseEntity.ok(userService.changeUserRoleById(
                    accessToken,
                    user.getId(),
                    user.getRole()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
