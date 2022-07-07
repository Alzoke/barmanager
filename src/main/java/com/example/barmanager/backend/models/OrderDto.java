package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"OrderName","itemsCounter","OrderStatus"})
public class OrderDto
{
    @JsonIgnore
    private final Order order;

    public String getOrderName()
    {
        return String.format("%s's order",order.getCustomer().getFirstName());
    }

    public int numberOfOrderedItems()
    {
        return order.getOrderedDrinks().size();
    }

    public String getOrderStatus()
    {
        return order.getOrderStatus().toString();
    }
}
