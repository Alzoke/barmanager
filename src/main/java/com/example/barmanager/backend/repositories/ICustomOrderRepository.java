package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Order;
import org.bson.Document;

import java.util.List;


public interface ICustomOrderRepository
{
    Order saveNewOrder(Order order);
    List<Document> getMostOrderedDrinks();
    List<Document> getProfitsByYear(int year);
    boolean deleteOrder(Order order);
}
