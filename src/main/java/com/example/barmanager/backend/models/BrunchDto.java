package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Value
@JsonPropertyOrder({"brunchName","brunchId","numOfOrders","totalOrdersBill","numOfEmployees","employeesIds","orderIds"})
public class BrunchDto
{
    @JsonIgnore
    private final Brunch brunch;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public String getBrunchName()
    {
        return String.format("%s brunch",brunch.getBrunchName());
    }

    public String getBrunchId()
    {
        return brunch.getId();
    }
    public int getNumOfOrders()
    {
        return brunch.getOrders().size();
    }

    public int getNumOfEmployees()
    {
        return brunch.getEmployeesIds().size();
    }


    public double getTotalOrdersBill()
    {

        double total = 0.0;
        for ( Order order : brunch.getOrders() )
        {
            total += order.getBill();
        }
        return Double.parseDouble(String.format("%.2f",total));
    }

    public List<String> getOrderIds(){
        List<String> orderIds = new ArrayList<>();
        brunch.getOrders().forEach(order -> orderIds.add(order.getOrderId()));

        return orderIds;
    }

    public List<String> getEmployeesIds(){
        List<String> employeesIds = new ArrayList<>();
        for ( String employeesId : brunch.getEmployeesIds() )
        {
            employeesIds.add(employeesId);
        }

        return employeesIds;

    }


}
