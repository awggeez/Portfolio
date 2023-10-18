package com.kpo.restaurant_project.domain.repository;

import com.kpo.restaurant_project.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByEmailAndPasswordHash(String username, String email);
}
