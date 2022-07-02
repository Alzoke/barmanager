package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Document("ingredients")
@JsonRootName("Drink")
public class DrinkIngredients {
    @Id
    @JsonProperty("strIngredient1")
    String strIngredient;

    @JsonRootName("Root")
    public static class ingredientList{
        @JsonProperty("drinks")
        public ArrayList<DrinkIngredients> drinkIngredients;
    }
}
