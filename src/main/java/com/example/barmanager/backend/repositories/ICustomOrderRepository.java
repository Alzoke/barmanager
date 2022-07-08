package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.List;


public interface ICustomOrderRepository
{
    void saveNewOrder(Order order, Customer customer);
    List<Document> getMostOrderedDrinks();
}
