package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.ApiDrinkAssembler;
import com.example.barmanager.backend.exceptions.DrinkNotFoundException;
import com.example.barmanager.backend.models.ApiDrink;
import com.example.barmanager.backend.service.CocktailDBAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
public class DrinkSupplierController {
    private final Logger logger;
    private final CocktailDBAPIService drinkService;
    private final ApiDrinkAssembler apiDrinkAssembler;

    public DrinkSupplierController(CocktailDBAPIService drinkService, ApiDrinkAssembler apiDrinkAssembler) {
        this.drinkService = drinkService;
        this.apiDrinkAssembler = apiDrinkAssembler;
        this.logger = LoggerFactory.getLogger(DrinkSupplierController.class);
    }

    /**
     * The method retrieves the drink from the CocktailDBAPI via CocktailDBAPIService
     * @param id The id of the requested drink
     * @return ApiDrink corresponding to the Drink with the requested ID
     *         or DrinkNotFoundException
     */
    @GetMapping("/supplier/{id}")
    public ResponseEntity<?> getDrink(@PathVariable String id) {
        CompletableFuture<ApiDrink> drinkCompletableFuture = drinkService.fetchDrinkById(id);
        try {
            ApiDrink drink = drinkCompletableFuture.get();
            if (drink == null){
                throw new DrinkNotFoundException(id);
            }
            return ResponseEntity.ok(apiDrinkAssembler.toModel(drink));
        } catch (InterruptedException | ExecutionException | DrinkNotFoundException e) {
            e.printStackTrace();
            throw new DrinkNotFoundException(id);
        }
    }

    /**
     * Retrieve drinks from CocktailDBAPI by category via CocktailDBAPIService
     * @param category category of the requested drinks
     * @return ResponseEntity.ok and Collection of entity models of ApiDrink in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/category")
    public ResponseEntity<?> getAllDrinksByCategory(@RequestParam String category){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksListByCategory(category);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null) {
                drinks = new ArrayList<>();
                logger.info("Couldn't find any drinks which correspond to the following category : " + category);
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while fetching Drinks by category");
        }
    }

    /**
     * Retrieve drinks from CocktailDBAPI by ingredient via CocktailDBAPIService
     * @param ingredient ingredient name of the requested drinks
     * @return ResponseEntity.ok and Collection of entity models of ApiDrink in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/ingredient")
    public ResponseEntity<?> getAllDrinksByIngredient(@RequestParam String ingredient){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksListByIngredient(ingredient);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null){
                drinks = new ArrayList<>();
                logger.info("Couldn't find any drinks which correspond to the following ingredient : " + ingredient);
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while fetching Drinks by ingredient");
        }
    }

    /**
     * Retrieve drinks from CocktailDBAPI by alcoholic filter via CocktailDBAPIService
     * @param alcoholicFilter alcoholic filter of the requested drinks
     * @return ResponseEntity.ok and Collection of entity models of ApiDrink in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/alcoholic")
    public ResponseEntity<?> getAllDrinksByAlcoholic(@RequestParam String alcoholicFilter){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksByAlcoholic(alcoholicFilter);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null){
                drinks = new ArrayList<>();
                logger.info("Couldn't find any drinks which correspond to the following alcoholic filter : " + alcoholicFilter);
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while fetching Drinks by alcoholic filter");
        }
    }

    /**
     * Retrieve all drinks from CocktailDBAPI via CocktailDBAPIService
     * @return ResponseEntity.ok and Collection of entity models of ApiDrink in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/")
    public ResponseEntity<?> getAllDrinks() {
        try {
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinkService.fetchAllDrinks().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while retrieved all drinks");
        }
    }

    /**
     * Retrieve all available categories from CocktailDBAPI
     * @return ResponseEntity.ok and a JSON which contains all category names in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/getCategories")
    public ResponseEntity<?> getAllCategories(){
        try {
            return ResponseEntity.ok(drinkService.fetchDrinkCategories().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while retrieving categories");
        }
    }

    /**
     * Retrieve all available ingredients from CocktailDBAPI
     * @return ResponseEntity.ok and a JSON which contains all ingredient names in the body,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/getIngredients")
    public ResponseEntity<?> getAllIngredients(){
        try {
            return ResponseEntity.ok(drinkService.fetchDrinkIngredients().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while retrieving ingredients");
        }
    }

    /**
     * Retrieve drinks from the CocktailDBAPI via CocktailDBAPIService,
     * the function returns multiple drinks if the name is contained in them.
     * @param name The requested drink's name
     * @return ResponseEntity.ok and collection of entity models of ApiDrink,
     *         or DrinkNotFoundException if there is no drinks with the corresponding name.
     */
    @GetMapping("/supplier/name")
    public ResponseEntity<?> getDrinkByName(@RequestParam String name){
        try {
            List<ApiDrink> drinks = drinkService.fetchDrinkByName(name).get();

            if (drinks == null){
                throw new DrinkNotFoundException(name);
            }

            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DrinkNotFoundException(name);
        }
    }

    /**
     * Get random drink from CocktailDBAPI via CocktailDBAPIService
     * @return ResponseEntity.ok and Entity model of ApiDrink,
     *         or ResponseEntity.internalServerError if something went wrong.
     */
    @GetMapping("/supplier/random")
    public ResponseEntity<?> getRandomDrink(){
        try {
            return ResponseEntity.ok(apiDrinkAssembler.toModel(drinkService.fetchRandomDrink().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong while retrieving a random drink");
        }
    }

}
