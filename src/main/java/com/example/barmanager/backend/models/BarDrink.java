package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("barinventory")
public class BarDrink{
    @Id
    public String id;
    public String name;
    public String category;
    public String isAlcoholic;
    public List<String> ingredients;
    public String image;
    public Double price;
    public String recommendedGlass;

    public BarDrink(String id, String name, String category, String isAlcoholic,
                    List<String> ingredients, String image, Double price, String recommendedGlass) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isAlcoholic = isAlcoholic;
        this.ingredients = ingredients;
        this.image = image;
        this.price = price;
        this.recommendedGlass = recommendedGlass;
    }
}
