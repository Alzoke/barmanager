package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Order;
import org.bson.Document;

import java.util.List;
import java.util.Optional;


public interface ICustomOrderRepository {
    Order saveNewOrder(Order order);

    List<Document> getTenMostOrderedDrinks();

    List<Document> getProfitsByYear(int year);

    boolean deleteOrder(Order order);

    Order closeOrder(Order order);

    Optional<Order> findCloseBySeat(int seatNumber);

}
