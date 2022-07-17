package com.example.barmanager.backend.exceptions;

public class BranchNotFoundException extends RuntimeException
{

    public BranchNotFoundException()
    {
        super(String.format("Cant find Branch"));

    }
    public BranchNotFoundException(String id)
    {
        super(String.format("Cant find Branch with corresponding id: %s",id));

    }

}
