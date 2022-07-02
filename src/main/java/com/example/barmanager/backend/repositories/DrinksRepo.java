package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Drink;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrinksRepo extends MongoRepository<Drink, String> {
}
