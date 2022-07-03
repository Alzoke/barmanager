package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Person {
    private int idNumber;
    private String name;


    public Person(String name,int idNumber) {
        this.name = name;
        this.idNumber = idNumber;

    }

}
