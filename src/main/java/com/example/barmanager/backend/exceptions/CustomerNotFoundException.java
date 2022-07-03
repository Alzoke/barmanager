package com.example.barmanager.backend.exceptions;

public class CustomerNotFoundException extends RuntimeException
{
    public CustomerNotFoundException(String id)
    {
        super(String.format("Cant find customer with corresponding id: %s",id));
    }
}
