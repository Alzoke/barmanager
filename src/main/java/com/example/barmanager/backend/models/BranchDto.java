package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Value
@JsonPropertyOrder({"branchName","branchId","numOfOrders","totalOrdersBill","numOfEmployees","employeesIds","orderIds"})
public class BranchDto
{
    @JsonIgnore
    private final Branch branch;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public String getBranchName()
    {
        return String.format("%s", branch.getBranchName());
    }

    public String getBranchId()
    {
        return branch.getId();
    }
    public int getNumOfOrders()
    {
        return branch.getOrders().size();
    }

    public int getNumOfEmployees()
    {
        return branch.getEmployeesIds().size();
    }


    public double getTotalOrdersBill()
    {

        double total = 0.0;
        for ( Order order : branch.getOrders() )
        {
            total += order.getBill();
        }
        return Double.parseDouble(String.format("%.2f",total));
    }

    public List<String> getOrderIds(){
        List<String> orderIds = new ArrayList<>();
        branch.getOrders().forEach(order -> orderIds.add(order.getOrderId()));

        return orderIds;
    }

    public List<String> getEmployeesIds(){
        List<String> employeesIds = new ArrayList<>();
        for ( String employeesId : branch.getEmployeesIds() )
        {
            employeesIds.add(employeesId);
        }

        return employeesIds;

    }


}
