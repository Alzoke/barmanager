package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.DrinkIngredients;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrinkIngredientsRepo extends MongoRepository<DrinkIngredients, String> {
}
