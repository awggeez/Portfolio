package com.kpo.restaurant_project.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class JwtDto {
    private String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
