package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.queryresults.DrinkCount;
import org.springframework.stereotype.Component;

import java.util.List;


public interface ICustomOrderRepository
{
    void saveNewOrder(Order order);
//    void saveNewOrder(Order order, Customer customer);
    List<DrinkCount> getMostOrderedDrinks();
}
