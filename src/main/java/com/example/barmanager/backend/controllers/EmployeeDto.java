package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.models.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
@JsonPropertyOrder({"fullName","salaryPerHour","brunches"})
public class EmployeeDto
{
    @JsonIgnore
    private final Employee employee;

    public String getFullName(){
        return String.format("%s %s", employee.getFirstName(),employee.getLastName());
    }

    public double getSalaryPerHour()
    {
        return employee.getSalaryPerHour();
    }

    public Map<String,String> getBrunches(){
        Map<String,String> brunches = new HashMap<>();

        employee.getBrunches()
                .forEach(brunch -> brunches.put(brunch.getBrunchName(),brunch.getId()));

        return brunches;
    }


}
