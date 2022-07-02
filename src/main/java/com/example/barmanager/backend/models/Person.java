package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Person {
    private Long id;
    private String name;
    private String role;

    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }
}
