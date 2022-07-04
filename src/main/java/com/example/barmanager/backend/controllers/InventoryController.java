package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BarDrinkAssembler;
import com.example.barmanager.backend.exceptions.DrinkNotFoundException;
import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.repositories.InventoryRepo;
import com.example.barmanager.backend.service.CocktailDBAPIService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class InventoryController {

    private final InventoryRepo barDrinkRepo;
    private final BarDrinkAssembler barDrinkAssembler;
    private final CocktailDBAPIService cocktailAPIService;

    public InventoryController(InventoryRepo barDrinkRepo, BarDrinkAssembler barDrinkAssembler, CocktailDBAPIService cocktailAPIService) {
        this.barDrinkRepo = barDrinkRepo;
        this.barDrinkAssembler = barDrinkAssembler;
        this.cocktailAPIService = cocktailAPIService;
    }

    @PostMapping("/inventory/")
    public ResponseEntity<EntityModel<BarDrink>> createBarDrink(@RequestBody BarDrink barDrink){
        return ResponseEntity.ok(barDrinkAssembler.toModel(barDrinkRepo.save(barDrink)));
    }

    @GetMapping("/inventory/{id}")
    public ResponseEntity<?> getBarDrink(@PathVariable String id) {
       return barDrinkRepo.findById(id)
               .map(barDrinkAssembler::toModel)
               .map(ResponseEntity::ok)
               .orElseThrow(()-> new DrinkNotFoundException(id));
    }

    @GetMapping("/inventory")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinks() {
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(barDrinkRepo.findAll()));

    }

    //@DeleteMapping("/inventory")

//    @GetMapping("/inventory/categories")
//    public ResponseEntity<List<DrinkCategories>> showDrinksCategories()
//    {
//        CompletableFuture<List<DrinkCategories>> categoriesFuture = cocktailAPIService.fetchDrinkCategories();
//        List<DrinkCategories> drinkCategories = null;
//        try
//        {
//            return ResponseEntity.ok(cocktailAPIService.fetchDrinkCategories().get());
//        }
//        catch (InterruptedException | ExecutionException e)
//        {
//            e.printStackTrace();
//        }
//        System.out.println(drinkCategories);
//        return null;
//    }
}
