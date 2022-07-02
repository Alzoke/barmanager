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
@Document("categories")
@JsonRootName("Drink")
public class DrinkCategories {
    @Id
    String strCategory;

    @JsonRootName("Root")
    public static class categoryList{
        @JsonProperty("drinks")
        public ArrayList<DrinkCategories> categories;
    }
}

