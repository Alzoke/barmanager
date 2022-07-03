package com.example.barmanager.backend.exceptions;

public class OrderNotFoundException extends RuntimeException
{
    public OrderNotFoundException(String orderId)
    {
        super(String.format("Cant find order with corresponding id: %s",orderId));
    }
}
