package com.example.barmanager.backend.exceptions;

public class BrunchNotFoundException extends RuntimeException
{
    public BrunchNotFoundException(String id)
    {
        super(String.format("Cant find Brunch with corresponding id: %s",id));

    }
}
