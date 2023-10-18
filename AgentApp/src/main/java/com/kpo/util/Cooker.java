package com.kpo.util;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cooker {
    private int id;
    private String name;
    private boolean isActive;
}