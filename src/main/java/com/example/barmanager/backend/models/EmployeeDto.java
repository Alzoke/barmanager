package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.util.List;

@Value
@JsonPropertyOrder({"id","fullName","salaryPerHour","branches"})
public class EmployeeDto
{
    @JsonIgnore
    private final Employee employee;

    public String getId(){
        return employee.getId();
    }

    public String getFullName(){
        return String.format("%s %s", employee.getFirstName(),employee.getLastName());
    }

    public double getSalaryPerHour()
    {
        return employee.getSalaryPerHour();
    }

    public List<Branch> getBrunches(){
       /* Map<String,String> brunches = new HashMap<>();

        employee.getBranches()
                .forEach(brunch -> brunches.put(brunch.getBranchName(),brunch.getId()));*/


        return employee.getBranches();
//        return brunches;
    }


}
