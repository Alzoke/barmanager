package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryRepo extends MongoRepository<BarDrink, String> {
    public List<BarDrink> findAllByCategory(String category);
    public List<BarDrink> findAllByIngredientsContains(String ingredient);
    public default BarDrink updateDrink(String id, BarDrink newDrink){
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
    public List<BarDrink> findByPriceBetween(Double lower, Double upper);
}
