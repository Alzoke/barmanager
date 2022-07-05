package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InventoryRepo extends MongoRepository<BarDrink, String> {

    List<BarDrink> findAllByCategory(String category);
    List<BarDrink> findAllByIngredientsContains(String ingredient);
    default BarDrink updateDrink(String id, BarDrink newDrink){
        BarDrink oldDrink = findById(id).orElse(newDrink);

        //new BarDrink was created because the DB didn't have drink with the corresponding id.
        if (oldDrink.equals(newDrink)){
            oldDrink.setId(id);
            save(oldDrink);
            return oldDrink;
        }

        UpdateUtil.copyNullProperties(newDrink, oldDrink);
        save(oldDrink);
        return oldDrink;
    }
    List<BarDrink> findByPriceBetween(Double lower, Double upper);
    List<BarDrink> findByOrderByPriceAsc();
}
