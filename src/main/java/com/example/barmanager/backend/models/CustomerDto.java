package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"name","ordersAmount"})
public class CustomerDto
{
   @JsonIgnore
   private final Customer customer;

    public String getName()
    {
        return customer.getName();
    }

    public int getOrderAmount()
    {
        return customer.getOrders().size();
    }

}
