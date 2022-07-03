package com.example.barmanager.backend.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;


@Data
@Document("Orders")
public class Order
{
    @Id private String orderId;
    private ArrayList<String> drinkIds;
    private double bill;
    private eOrderStatus orderStatus;

    /*@DocumentReference
    private Customer customer;*/

    public Order()
    {
        orderStatus = eOrderStatus.Open;
        drinkIds = new ArrayList<>();
    }
}
