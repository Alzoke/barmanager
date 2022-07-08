package com.example.barmanager.backend.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;


@Data
@Document("orders")
public class Order
{
    @Id private String orderId;

    @DocumentReference
    private ArrayList<BarDrink> orderedDrinks;
    private double bill;
    private eOrderStatus orderStatus;

    public double getBill()
    {
      return bill;
    }

    public void setBill()
    {
        for ( BarDrink barDrink : orderedDrinks )
        {
            this.bill += barDrink.getPrice();
        }

    }

    @DocumentReference
    private Customer customer;

    public Order()
    {
        orderStatus = eOrderStatus.Open;
        this.orderedDrinks = new ArrayList<>();

    }
    public void addDrinkToOrder(BarDrink barDrink)
    {
        this.orderedDrinks.add(barDrink);
        this.setBill(getBill() + barDrink.getPrice());
    }
    public Order(Customer customer)
    {
        this();
        this.customer = customer;
        setBill();

    }
    public Order(Customer customer,ArrayList<BarDrink> orderedDrinks)
    {
        this();
        this.customer = customer;
        this.orderedDrinks = orderedDrinks;
        setBill();

    }


}
