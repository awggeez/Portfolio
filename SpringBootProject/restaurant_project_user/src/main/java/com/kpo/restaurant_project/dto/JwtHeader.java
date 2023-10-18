package com.kpo.restaurant_project.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class JwtHeader {
    private String typ;
    private String alg;
}
