package com.example.barmanager.backend.service;

import com.example.barmanager.backend.models.Drink;
import com.example.barmanager.backend.models.DrinkCategories;
import com.example.barmanager.backend.models.DrinkIngredients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CocktailDBAPIService {
    private final RestTemplate template;
    private final Logger logger;

    public CocktailDBAPIService(RestTemplateBuilder templateBuilder) {
        this.logger = LoggerFactory.getLogger(CocktailDBAPIService.class);
        this.template = templateBuilder.build();
    }


    @Async
    public CompletableFuture<List<DrinkIngredients>> fetchDrinkIngredients(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";
        DrinkIngredients.ingredientList ingredientResponse = this.template.getForObject(url, DrinkIngredients.ingredientList.class);
        List<DrinkIngredients> ingredients = new ArrayList<>(ingredientResponse.drinkIngredients);

        return CompletableFuture.completedFuture(ingredients);
    }

    @Async
    public CompletableFuture<List<DrinkCategories>> fetchDrinkCategories(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list";
        DrinkCategories.categoryList categoriesResponse = this.template.getForObject(url, DrinkCategories.categoryList.class);
        List<DrinkCategories> categories = new ArrayList(categoriesResponse.categories);

        return CompletableFuture.completedFuture(categories);
    }

    @Async
    public CompletableFuture<List<Drink>> fetchDrinksListByCategory(String categoryName){
        this.logger.info("Fetching drinks with category : " + categoryName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=%s", categoryName);
        Drink.DrinkList response = this.template.getForObject(url, Drink.DrinkList.class);
        List<Drink> drinks = new ArrayList(response.drinks);

        return CompletableFuture.completedFuture(drinks);
    }

    @Async
    public CompletableFuture<List<Drink>> fetchDrinksListByIngredient(String ingredientName){
        this.logger.info("Fetching drinks with ingredient : " + ingredientName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=%s", ingredientName);
        Drink.DrinkList response = this.template.getForObject(url, Drink.DrinkList.class);
        logger.info(response.drinks.toString());
        List<Drink> drinks = new ArrayList(response.drinks);

        return CompletableFuture.completedFuture(drinks);
    }

    @Async
    public CompletableFuture<Drink> fetchCocktailById(String id){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=%s", id);
        Drink.DrinkList cocktailList = this.template.getForObject(url, Drink.DrinkList.class);
        Drink cocktail = cocktailList.drinks.get(0);

        return CompletableFuture.completedFuture(cocktail);
    }

    @Async
    public CompletableFuture<List<Drink>> fetchCocktailByName(String cocktailName){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=%s", cocktailName);
        Drink.DrinkList cocktailList = this.template.getForObject(url, Drink.DrinkList.class);
        List<Drink> cocktail = cocktailList.drinks;

        return CompletableFuture.completedFuture(cocktail);
    }
}
