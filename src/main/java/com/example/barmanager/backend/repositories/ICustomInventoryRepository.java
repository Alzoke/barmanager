package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public interface ICustomInventoryRepository {
    List<Document> getCountGroupByCategory();

    Iterable<? extends BarDrink> getFilteredByMultipleParams(Optional<String> category, Optional<String> ingredient,
                                                             Optional<String> alcoholFilter, Optional<Double> minPrice,
                                                             Optional<Double> maxPrice);

}
