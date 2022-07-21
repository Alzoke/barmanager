package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Person {
    private int idNumber;
    private String firstName;
    private String lastName;

    public Person(int idNumber, String firstName, String lastName) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
