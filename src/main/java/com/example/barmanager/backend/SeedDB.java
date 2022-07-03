package com.example.barmanager.backend;

import com.example.barmanager.backend.models.Drink;
import com.example.barmanager.backend.models.DrinkCategories;
import com.example.barmanager.backend.models.DrinkDTO;
import com.example.barmanager.backend.models.DrinkIngredients;
import com.example.barmanager.backend.repositories.DrinkCategoryRepo;
import com.example.barmanager.backend.repositories.DrinkIngredientsRepo;
import com.example.barmanager.backend.repositories.DrinksRepo;
import com.example.barmanager.backend.service.CocktailDBAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SeedDB implements CommandLineRunner{
    private final CocktailDBAPIService drinkService;
    private final Logger logger;
    private final DrinkCategoryRepo drinkCategoryRepo;
    private final DrinkIngredientsRepo drinkIngredientsRepo;
    private final DrinksRepo drinksRepo;

    public SeedDB(CocktailDBAPIService drinkService, DrinkCategoryRepo drinkCategoryRepo,
                  DrinkIngredientsRepo drinkIngredientsRepo, DrinksRepo drinksRepo) {
        this.drinkService = drinkService;
        this.drinksRepo = drinksRepo;
        this.drinkIngredientsRepo = drinkIngredientsRepo;
        this.drinkCategoryRepo = drinkCategoryRepo;
        this.logger = LoggerFactory.getLogger(SeedDB.class);
    }


    @Override
    public void run(String... args) throws Exception {

        //Seed drink category collection
        CompletableFuture<List<DrinkCategories>> categoriesFuture = drinkService.fetchDrinkCategories();
        List<DrinkCategories> drinkCategories = categoriesFuture.get();
        drinkCategoryRepo.saveAll(drinkCategories);

        //Seed drink ingredients collection
        CompletableFuture<List<DrinkIngredients>>  ingredientsFuture = drinkService.fetchDrinkIngredients();
        List<DrinkIngredients> drinkIngredients = ingredientsFuture.get();
        drinkIngredientsRepo.saveAll(drinkIngredients);

        CompletableFuture<List<Drink>> reducedDrinkObjectsFuture;
        CompletableFuture<List<Drink>> completedDrinkObject;
        List<Drink> currentDrinks;

        //Fetch all drinks by category
        this.logger.info("Fetching all drinks from the API , Current time : " + LocalDateTime.now());
        for (DrinkCategories categoryName: drinkCategories) {
            reducedDrinkObjectsFuture = drinkService.fetchDrinksListByCategory(categoryName.getStrCategory());
            currentDrinks = reducedDrinkObjectsFuture.get();

            for (Drink drink: currentDrinks) {
                completedDrinkObject = drinkService.fetchCocktailByName(drink.strDrink);
                drinksRepo.saveAll(completedDrinkObject.get());
            }
        }
        this.logger.info("Completed fetching all drinks from the API , Current time : " + LocalDateTime.now());

        //Fetch all drinks by ingredient
        this.logger.info("Fetching all drinks from the API , Current time : " + LocalDateTime.now());
        for (DrinkIngredients ingredientName: drinkIngredients) {
            reducedDrinkObjectsFuture = drinkService.fetchDrinksListByIngredient(ingredientName.getStrIngredient());
            currentDrinks = reducedDrinkObjectsFuture.get();

            for (Drink drink: currentDrinks) {
                completedDrinkObject = drinkService.fetchCocktailByName(drink.strDrink);
                drinksRepo.saveAll(completedDrinkObject.get());
            }
        }
        this.logger.info("Completed fetching all drinks from the API , Current time : " + LocalDateTime.now());

    }
}
