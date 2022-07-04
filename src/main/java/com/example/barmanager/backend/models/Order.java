package com.example.barmanager.backend.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;


@Data
@Document("Orders")
public class Order
{
    @Id private String orderId;

    @DocumentReference
    private ArrayList<BarDrink> orderedDrinks;
    private double bill;
    private eOrderStatus orderStatus;

    @DocumentReference
    private Customer customer;

    public Order()
    {
        orderStatus = eOrderStatus.Open;
    }

    public Order(Customer customer,ArrayList<BarDrink> orderedDrinks)
    {
        this();
        this.customer = customer;
        this.orderedDrinks = orderedDrinks;

    }


}
