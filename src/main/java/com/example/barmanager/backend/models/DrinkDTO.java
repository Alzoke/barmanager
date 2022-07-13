package com.example.barmanager.backend.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;


@Value
@JsonPropertyOrder({"id","name","category","isAlcoholic","ingredients","price","image"})
public class DrinkDTO  {
    @JsonIgnore
    private final ApiDrink drink;

    public List<String> getIngredients() throws NoSuchFieldException, IllegalAccessException {
        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String fieldName = String.format("strIngredient%s", (i + 1));
            Object fieldValue = drink.getClass().getDeclaredField(fieldName).get(drink);
            if (fieldValue != null) {
                ingredients.add(fieldValue.toString());
            }
        }

        return ingredients;
    }

    public String getId(){
        return drink.getIdDrink();
    }

    public String getName(){
        return drink.getStrDrink();
    }

    public String getDrinkImg(){
        return drink.getStrDrinkThumb();
    }

    public String getCategory(){
        return drink.getStrCategory();
    }

    public String getIsAlcoholic(){
        return drink.strAlcoholic;
    }
    public Double getPrice() throws NoSuchFieldException, IllegalAccessException {
        Double ingredientCount = 0.0;

        for (int i = 0; i < 15; i++) {
            String fieldName = String.format("strIngredient%s",(i+1));
            if (drink.getClass().getDeclaredField(fieldName).get(drink) != null){
                ingredientCount++;
            }
        }
        return ingredientCount * 10;
    }

}
