package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"name", "idNumber", "ordersAmount"})
public class CustomerDto {
    @JsonIgnore
    private final Customer customer;

    public String getName() {
        return
                customer.getFirstName() + " " + customer.getLastName();
    }

    public int getIdNumber() {
        return customer.getIdNumber();
    }

    public int getOrderAmount() {
        return customer.getOrdersIds().size();
    }

}
