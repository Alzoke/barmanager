package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"id","name","category"})
public class DrinkDTO {
    @JsonIgnore
    private final Drink drink;

    public String getId(){
        return drink.getIdDrink();
    }

    public String getName(){
        return drink.getStrDrink();
    }

    public String getCategory(){
        return drink.getStrCategory();
    }
}
