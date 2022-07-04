package com.example.barmanager.backend.exceptions;

public class DrinkNotFoundException extends RuntimeException{
    public DrinkNotFoundException(String id){
        super("There is no product corresponding to id = " + id);
    }
}
