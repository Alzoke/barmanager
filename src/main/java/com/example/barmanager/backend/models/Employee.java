package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@Document("Employees")
@NoArgsConstructor
public class Employee extends Person
{
    @Id private String id;
    private double salaryPerHour;
    @DocumentReference
    private List<Brunch> brunches;

    public Employee(int idNumber, String firstName, String lastName, double salaryPerHour)
    {
        super(idNumber, firstName, lastName);
        this.salaryPerHour = salaryPerHour;
        brunches = new ArrayList<>();
    }

    public void addToBrunch(Brunch brunch)
    {
        brunches.add(brunch);
    }

    public void removeFromBrunch(Brunch brunch)
    {
        brunches.remove(brunch);
    }
}
