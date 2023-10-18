package com.kpo.restaurant_project_order.dto;

import com.kpo.restaurant_project_order.domain.models.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class JwtPayload {
    private String issuer;
    private Long expiration;
    private Long issuedAt;
    Role role;
    private String username;
}
