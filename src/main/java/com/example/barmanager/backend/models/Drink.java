package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Document("drinks")
public class  Drink{
    @Id
    public String idDrink;
    public String strDrink;
    public Object strDrinkAlternate;
    public String strTags;
    public String strCategory;

    public String strIBA;
    public String strAlcoholic;
    public String strGlass;
    @JsonIgnore
    public String strInstructions;

    public String strDrinkThumb;

    public String strIngredient1;

    public String strIngredient2;

    public String strIngredient3;

    public String strMeasure1;

    public String strMeasure2;

    public String strMeasure3;

    @JsonRootName("Root")
    public static class DrinkList{
        public ArrayList<Drink> drinks;
    }
}


