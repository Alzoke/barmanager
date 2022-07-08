package com.example.barmanager.backend.exceptions;

public class NewDrinkCreationException extends IllegalArgumentException
{
    public NewDrinkCreationException(String msg)
    {
        super(msg);
    }
}
