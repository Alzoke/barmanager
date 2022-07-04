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
    //private final DrinkAssembler drinkAssembler;
    private final ApiDrinkAssembler apiDrinkAssembler;

    public DrinkSupplierController(CocktailDBAPIService drinkService, ApiDrinkAssembler apiDrinkAssembler) {
        this.drinkService = drinkService;
        this.apiDrinkAssembler = apiDrinkAssembler;
        this.logger = LoggerFactory.getLogger(DrinkSupplierController.class);
    }

    /**
     * The method retrieves the drink from the CocktailDBAPI via DrinkService ,
     * if the retrieval from the service fails ,the method retrieves the drink from the DrinkRepository
     * @param id
     * @return DrinkDTO corresponding to the Drink with the requested ID.
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

    @GetMapping("/supplier/category")
    public ResponseEntity<?> getAllDrinksByCategory(@RequestParam String param){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksListByCategory(param);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null){
                drinks = new ArrayList<>();
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/supplier/ingredient")
    public ResponseEntity<?> getAllDrinksByIngredient(@RequestParam String param){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksListByIngredient(param);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null){
                drinks = new ArrayList<>();
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/supplier/alcoholic")
    public ResponseEntity<?> getAllDrinksByAlcoholic(@RequestParam String param){
        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksByAlcoholic(param);
        try {
            List<ApiDrink> drinks = drinksFuture.get();

            if (drinks == null){
                drinks = new ArrayList<>();
            }
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/supplier/")
    public ResponseEntity<?> getAllDrinks() {
        try {
            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinkService.fetchAllDrinks().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/supplier/getCategories")
    public ResponseEntity<?> getAllCategories(){
        try {
            return ResponseEntity.ok(drinkService.fetchDrinkCategories().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/supplier/getIngredients")
    public ResponseEntity<?> getAllIngredients(){
        try {
            return ResponseEntity.ok(drinkService.fetchDrinkIngredients().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/supplier/name")
    public ResponseEntity<?> getDrinkByName(@RequestParam String param){
        try {
            List<ApiDrink> drinks = drinkService.fetchDrinkByName(param).get();

            if (drinks == null){
                throw new DrinkNotFoundException(param);
            }

            return ResponseEntity.ok(apiDrinkAssembler.toCollectionModel(drinks));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DrinkNotFoundException(param);
        }
    }

    @GetMapping("/supplier/random")
    public ResponseEntity<?> getRandomDrink(){
        try {
            return ResponseEntity.ok(apiDrinkAssembler.toModel(drinkService.fetchRandomDrink().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
