package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import org.springframework.stereotype.Component;


public interface ICustomOrderRepository
{
    void saveNewOrder(Order order, Customer customer);
}
