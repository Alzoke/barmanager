package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BarDrinkAssembler;
import com.example.barmanager.backend.exceptions.DrinkNotFoundException;
import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.repositories.ICustomInventoryRepository;
import com.example.barmanager.backend.repositories.InventoryRepo;
import com.example.barmanager.backend.service.InventoryService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Controller which is responsible for managing
 * and routing http requests for the inventory path
 */
@RestController
public class InventoryController {

    private final InventoryRepo inventoryRepo;
    private final ICustomInventoryRepository customInventoryRepository;
    private final BarDrinkAssembler barDrinkAssembler;
    private final InventoryService inventorService;
    private final Logger logger;

    public InventoryController(InventoryRepo barDrinkRepo, ICustomInventoryRepository customInventoryRepository, BarDrinkAssembler barDrinkAssembler, InventoryService inventorService) {
        this.inventoryRepo = barDrinkRepo;
        this.customInventoryRepository = customInventoryRepository;
        this.barDrinkAssembler = barDrinkAssembler;
        this.inventorService = inventorService;
        this.logger = LoggerFactory.getLogger(InventoryController.class);
    }

    /**
     * Handles Post requests for creating new BarDrink
     * @param barDrink the new BarDrink to be created and saved into the DB.
     * @return ResponseEntity.created with a link of the place in which the new BarDrink was saved in
     *         and Entity Model of the created drink,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @PostMapping("/inventory/")
    public ResponseEntity<?> createBarDrink(@RequestBody BarDrink barDrink){
        if ( inventorService.newDrinkValidation(barDrink) )
        {
            logger.info("Saving: " + barDrink);
            EntityModel<BarDrink> drinkEntity = barDrinkAssembler.toModel(inventoryRepo.save(barDrink));
            try {
                return ResponseEntity.created(new URI(
                        drinkEntity.getRequiredLink(IanaLinkRelations.SELF).getHref())).body(drinkEntity);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.internalServerError().body("Error while creating and saving the drink");
    }

    /**
     * Retrieve BarDrink with a specific id from the DB.
     * @param id The id of the requested BarDrink
     * @return ResponseEntity.ok and a EntityModel of the requested BarDrink in the body,
     *         or DrinkNotFoundException if there is no BarDrink with the corresponding id.
     */
    @GetMapping("/inventory/{id}")
    public ResponseEntity<?> getBarDrink(@PathVariable String id) {
       return inventoryRepo.findById(id)
               .map(barDrinkAssembler::toModel)
               .map(ResponseEntity::ok)
               .orElseThrow(()-> new DrinkNotFoundException(id));
    }

    /**
     * Retrieves all BarDrinks from the DB.
     * @return ResponseEntity.ok and a Collection of EntityModels of BarDrinks in the body.
     */
    @GetMapping("/inventory")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinks() {
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAll()));
    }

    /**
     * Check if the DB contains drink with the corresponding id , if not return DrinkNotFoundException
     * Otherwise delete the drink and return it as response.
     * @param id of the drink to be deleted
     * @return ResponseEntity.ok and EntityModel of BarDrink of the deleted drink in the body,
     *         or DrinkNotFoundException
     */
    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<EntityModel<BarDrink>> deleteDrink(@PathVariable String id){
        EntityModel<BarDrink> drinkEntity = inventoryRepo.findById(id)
                .map(barDrinkAssembler::toModel).orElseThrow(()-> new DrinkNotFoundException(id));
        inventoryRepo.deleteById(id);
        return ResponseEntity.ok(drinkEntity);
    }

    /**
     * Put requests handler , updates a drink or creates a new one if it doesn't exist in the DB.
     * @param id The id of the requested BarDrink to update.
     * @param body The fields which needs to be updated
     * @return ResponseEntity.created with the link of the place the drink was created/updated at
     *         and a EntityModel of the updated BarDrink in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @PutMapping("/inventory/{id}")
    public ResponseEntity<?> updateDrink(@PathVariable String id, @RequestBody BarDrink body){
       EntityModel<BarDrink> updatedDrink = barDrinkAssembler.toModel(inventoryRepo.updateDrink(id, body));

        try {
            return ResponseEntity.created(new URI(
                    updatedDrink.getRequiredLink(IanaLinkRelations.SELF).getHref())).body(updatedDrink);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().body("Something went wrong while updating");
    }


    /**
     * Retrieves all BarDrinks from the DB filtered by category.
     * @param category The category name of the requested BarDrinks
     * @return ResponseEntity.ok and a Collection of EntityModels of BarDrinks in the body of the response.
     */
    @GetMapping("/inventory/filterByCategory")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinksFilteredByCategory(@RequestParam String category){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAllByCategory(category)));
    }

    /**
     * Retrieves all BarDrinks from the DB filtered by ingredient.
     * @param ingredient The ingredient name of the requested BarDrinks
     * @return ResponseEntity.ok and a Collection of EntityModels of BarDrinks in the body of the response.
     */
    @GetMapping("/inventory/filterByIngredient")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getAllDrinksFilteredByIngredient(@RequestParam String ingredient){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findAllByIngredientsContains(ingredient)));
    }

    /**
     * Retrieves all BarDrinks from the DB filtered by price range.
     * @param min The minimum price of the BarDrinks.
     * @param max The maximum price of the BarDrinks.
     * @return ResponseEntity.ok and a Collection of EntityModels of BarDrinks in the body of the response.
     */
    @GetMapping("/inventory/filterByPriceRange")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getDrinksFilteredByPriceRange(@RequestParam Double min, Double max){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findByPriceBetween(min,max)));
    }

    /**
     * Retrieves all BarDrinks from the DB sorted by price.
     * @return ResponseEntity.ok and a Collection of EntityModels of BarDrinks in the body of the response.
     */
    @GetMapping("/inventory/sortedByPrice")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getDrinksSortedByPrice(){
        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(inventoryRepo.findByOrderByPriceAsc()));
    }

    /**
     * Retrieve a group count of the drinks in the inventory by category.
     * @return ResponseEntity.ok ,the response's body contains a List of documents
     *         when each document contains a category name, and its count.
     */
    @GetMapping("/inventory/groupCountByCategory/")
    public ResponseEntity<List<Document>> getInventoryCountByCategory(){
        return ResponseEntity.ok(customInventoryRepository.getCountGroupByCategory());
    }

    /**
     * Retrieve a group count of the drinks in the inventory by ingredient.
     * @return ResponseEntity.ok ,the response's body contains a List of documents
     *         when each document contains an ingredient name, and its count.
     */
    @GetMapping("/inventory/getIngredientsCount/")
    public ResponseEntity<List<Document>> getInventoryIngredientCount(){
        return ResponseEntity.ok(customInventoryRepository.getIngredientCount());
    }

    /**
     * Retrieve filtered drinks collection from the inventory by a set of optional predefined parameters as filter parameters.
     * @param category Optional , filter by category.
     * @param ingredient Optional , filter by ingredient.
     * @param alcoholFilter Optional , filter by alcoholic type.
     * @param minPrice Optional (default value is 0.0), filter by minimum price.
     * @param maxPrice Optional (default value id Double.MAX_VALUE), filter by maximum price
     * @return ResponseEntity.ok , and Collection of EntityModel of filtered BarDrinks in its body.
     */
    @GetMapping("/inventory/filter")
    public ResponseEntity<CollectionModel<EntityModel<BarDrink>>> getFilteredInventory(
                                                  @RequestParam Optional<String> category,
                                                  @RequestParam Optional<String> ingredient,
                                                  @RequestParam Optional<String> alcoholFilter,
                                                  @RequestParam Optional<Double> minPrice,
                                                  @RequestParam Optional<Double> maxPrice){

        return ResponseEntity.ok(barDrinkAssembler.toCollectionModel(
                customInventoryRepository.getFilteredByMultipleParams(category, ingredient, alcoholFilter, minPrice, maxPrice)));
    }
}
