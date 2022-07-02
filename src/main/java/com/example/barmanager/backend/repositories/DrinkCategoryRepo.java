package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.DrinkCategories;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrinkCategoryRepo extends MongoRepository<DrinkCategories,String> {
}
