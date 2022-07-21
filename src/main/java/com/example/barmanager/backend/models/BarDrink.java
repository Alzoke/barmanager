package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("barinventory")
public class BarDrink {
    @Id
    public String id;
    public String idInApi;
    public String name;
    public String category;
    public String isAlcoholic;
    public List<String> ingredients;
    public String image;
    public Double price;
    public String recommendedGlass;

    public BarDrink(String idInApi, String name, String category, String isAlcoholic,
                    List<String> ingredients, String image, Double price, String recommendedGlass) {
        this.idInApi = idInApi;
        this.name = name;
        this.category = category;
        this.isAlcoholic = isAlcoholic;
        this.ingredients = ingredients;
        this.image = image;
        this.price = price;
        this.recommendedGlass = recommendedGlass;
    }

    @Override
    public String toString() {
        return "BarDrink{" +
                "id='" + id + '\n' +
                ", idInApi='" + idInApi + '\n' +
                ", name='" + name + '\n' +
                ", category='" + category + '\n' +
                ", isAlcoholic='" + isAlcoholic + '\n' +
                ", ingredients=" + ingredients +
                ", image='" + image + '\n' +
                ", price=" + price +
                ", recommendedGlass='" + recommendedGlass + '\n' +
                '}';
    }
}
