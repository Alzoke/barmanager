package com.example.barmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ApiDrink {
    @Id
    public String idDrink;
    public String strDrink;
    public Object strDrinkAlternate;
    public String strTags;
    public Object strVideo;
    public String strCategory;
    public String strIBA;
    public String strAlcoholic;
    public String strGlass;
    public String strInstructions;
    public Object strInstructionsES;
    public String strInstructionsDE;
    public Object strInstructionsFR;
    public String strInstructionsIT;
    @JsonProperty("strInstructionsZH-HANS")
    public Object strInstructionsZHHANS;
    @JsonProperty("strInstructionsZH-HANT")
    public Object strInstructionsZHHANT;
    public String strDrinkThumb;
    public String strIngredient1;
    public String strIngredient2;
    public String strIngredient3;
    public String strIngredient4;
    public String strIngredient5;
    public String strIngredient6;
    public String strIngredient7;
    public Object strIngredient8;
    public Object strIngredient9;
    public Object strIngredient10;
    public Object strIngredient11;
    public Object strIngredient12;
    public Object strIngredient13;
    public Object strIngredient14;
    public Object strIngredient15;
    public String strMeasure1;
    public String strMeasure2;
    public String strMeasure3;
    public String strMeasure4;
    public String strMeasure5;
    public String strMeasure6;
    public String strMeasure7;
    public Object strMeasure8;
    public Object strMeasure9;
    public Object strMeasure10;
    public Object strMeasure11;
    public Object strMeasure12;
    public Object strMeasure13;
    public Object strMeasure14;
    public Object strMeasure15;
    public String strImageSource;
    public String strImageAttribution;
    public String strCreativeCommonsConfirmed;
    public String dateModified;


    @JsonRootName("Root")
    public static class DrinkList{
        public ArrayList<ApiDrink> drinks;
    }
}
