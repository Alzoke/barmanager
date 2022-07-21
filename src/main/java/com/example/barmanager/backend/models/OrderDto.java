package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;

@Value
@JsonPropertyOrder({"orderName", "orderDate ", "orderBill", "itemsCounter", "orderStatus", "orderedItems", "orderId"
        , "seatNumber"})
public class OrderDto {
    @JsonIgnore
    private final Order order;

    public String getOrderName() {
        return String.format("%s's order", order.getCustomer().getFirstName());
    }

    public String getOrderDate() {
        return String.valueOf(this.order.getOrderDate());
    }

    public double getOrderBill() {
        return order.getBill();
    }

    public String getOrderId() {
        return order.getOrderId();
    }

    public int getSeatNumber() {
        return order.getSeatNumber();
    }

    public ArrayList<BarDrink> getOrderedItems() {
        return order.getOrderedDrinks();
    }

    public int getItemsCounter() {
        return order.getOrderedDrinks().size();
    }

    public String getOrderStatus() {
        return order.getOrderStatus().toString();
    }
}
