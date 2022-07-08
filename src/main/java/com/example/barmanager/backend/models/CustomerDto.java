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

    public String getFullName()
    {
        return
                customer.getFirstName() + " " + customer.getLastName();
    }

    public int getOrderAmount()
    {
        return customer.getOrdersIds().size();
    }

}
