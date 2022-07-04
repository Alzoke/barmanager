package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryRepo extends MongoRepository<BarDrink, String> {
}
