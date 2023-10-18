package com.kpo.restaurant_project_order.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class InvalidOperationException extends Exception {
    private HttpStatus httpStatus;
    private String message;
}
