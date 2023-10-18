package com.kpo.restaurant_project.domain.repository;

import com.kpo.restaurant_project.domain.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {

    Session findBySessionToken(String refreshToken);

    Session findByUserId(Integer id);

}
