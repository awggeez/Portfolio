package com.kpo.restaurant_project.domain.models;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role {
    CUSTOMER,
    CHEF,
    MANAGER
}
