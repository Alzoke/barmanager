package com.example.barmanager.backend.exceptions;

public class BrunchNotFoundException extends RuntimeException
{

    public BrunchNotFoundException()
    {
        super(String.format("Cant find Branch"));

    }
    public BrunchNotFoundException(String id)
    {
        super(String.format("Cant find Branch with corresponding id: %s",id));

    }

}
