package com.example.barmanager.backend.service;

import com.example.barmanager.backend.exceptions.NewDrinkCreationException;
import com.example.barmanager.backend.models.BarDrink;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{
    public boolean newDrinkValidation(BarDrink barDrink)
    {
        if ( barDrink.getPrice() <= 0 || barDrink.getPrice() == null)
        {
            throw new NewDrinkCreationException("Drink must have positive price");
        }
        if ( barDrink.getName().isEmpty() || barDrink.getName() == null )
        {
            throw  new NewDrinkCreationException("Drink must have a name");
        }
        return true;

    }

}
