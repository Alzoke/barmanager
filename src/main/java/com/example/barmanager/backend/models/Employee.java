package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Document("Employees")
@NoArgsConstructor
public class Employee extends Person
{
    @Id private String id;
    private double salaryPerHour;
    @DocumentReference
    private List<Branch> branches;

    public Employee(int idNumber, String firstName, String lastName, double salaryPerHour)
    {
        super(idNumber, firstName, lastName);
        this.salaryPerHour = salaryPerHour;
        branches = new ArrayList<>();
    }

    public void addToBrunch(Branch brunch)
    {
        branches.add(brunch);
    }

    public void removeFromBrunch(Branch brunch)
    {
        branches.remove(brunch);
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o ) return true;
        if ( !(o instanceof Employee) ) return false;
        if ( !super.equals(o) ) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.getSalaryPerHour(), getSalaryPerHour()) == 0 && Objects.equals(getId(), employee.getId()) && Objects.equals(getBranches(), employee.getBranches());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), getId(), getSalaryPerHour(), getBranches());
    }
}
