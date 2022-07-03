package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Order;

public interface ICustomOrderRepository
{
    Order saveNewOrder(Order order);
}
