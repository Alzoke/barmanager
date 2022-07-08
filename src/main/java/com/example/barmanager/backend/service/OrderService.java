package com.example.barmanager.backend.service;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.repositories.CustomOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService
{
    private final CustomOrderRepository orderRepository;

    public OrderService(CustomOrderRepository orderRepository)
    {
        this.orderRepository = orderRepository;
    }

    public void saveNewOrder(Order order, Customer customer)
    {
        orderRepository.saveNewOrder(order);
    }
}
