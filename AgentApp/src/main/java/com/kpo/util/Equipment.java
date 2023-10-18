package com.kpo.util;


import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Equipment {
    private int id; // нововведения в json
    private int type;
    private String name;
    private boolean isActive;
}
