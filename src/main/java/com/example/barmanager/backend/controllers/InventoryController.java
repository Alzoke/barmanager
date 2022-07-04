package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BarDrinkAssembler;
import com.example.barmanager.backend.exceptions.DrinkNotFoundException;
import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.repositories.InventoryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class InventoryController {

    private final InventoryRepo inventoryRepo;
    private final BarDrinkAssembler barDrinkAssembler;
    private final Logger logger;


    public InventoryController(InventoryRepo barDrinkRepo, BarDrinkAssembler barDrinkAssembler) {
        this.inventoryRepo = barDrinkRepo;
        this.barDrinkAssembler = barDrinkAssembler;
        this.logger = LoggerFactory.getLogger(InventoryController.class);
    }

    @PostMapping("/inventory/")
    public ResponseEntity<?> createBarDrink(@RequestBody BarDrink barDrink){
        EntityModel<BarDrink> drinkEntity = barDrinkAssembler.toModel(inventoryRepo.save(barDrink));
        try {
            return ResponseEntity.created(new URI(
                    drinkEntity.getRequiredLink(IanaLinkRelations.SELF).getHref())).body(drinkEntity);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error while creating and saving the drink");
        }
    }

    @GetMapping("/inventory/{id}")
    public ResponseEntity<?> getBarDrink(@PathVariable String id) {
       return inventoryRepo.findById(id)
               .map(barDrinkAssembler::toModel)
               .map(ResponseEntity::ok)
               .orElseThrow(()-> new DrinkNotFoundException(id));
    }

    @GetMapping("/inventory")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinks() {
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAll()));

    }

    /**
     * Check if the DB contains drink with the corresponding id , if not return DrinkNotFoundException
     * Otherwise delete the drink and return it as response.
     * @param id of the drink to be deleted
     * @return ResponseEntity.ok(BarDrink Entity of the deleted drink) or DrinkNotFoundException
     */
    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<EntityModel<BarDrink>> deleteDrink(@PathVariable String id){
        EntityModel<BarDrink> drinkEntity = inventoryRepo.findById(id)
                .map(barDrinkAssembler::toModel).orElseThrow(()-> new DrinkNotFoundException(id));
        inventoryRepo.deleteById(id);
        return ResponseEntity.ok(drinkEntity);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<EntityModel<BarDrink>> updateDrink(@PathVariable String id, @RequestBody BarDrink body){
       return ResponseEntity.ok(barDrinkAssembler.toModel(inventoryRepo.updateDrink(id, body)));
    }

    @GetMapping("/inventory/filterByCategory")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinksFilteredByCategory(@RequestParam String category){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAllByCategory(category)));
    }

    @GetMapping("/inventory/filterByIngredient")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinksFilteredByIngredient(@RequestParam String ingredient){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAllByIngredientsContains(ingredient)));
    }

    @GetMapping("/inventory/filterByPriceRange")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getDrinksFilteredByPriceRange(@RequestParam Double min, Double max){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findByPriceBetween(min,max)));
    }
}
