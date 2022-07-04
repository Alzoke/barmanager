package com.example.barmanager.backend.service;

import com.example.barmanager.backend.models.ApiDrink;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CocktailDBAPIService {
    private final RestTemplate template;
    private final Logger logger;

    public CocktailDBAPIService(RestTemplateBuilder templateBuilder) {
        this.logger = LoggerFactory.getLogger(CocktailDBAPIService.class);
        this.template = templateBuilder.build();
    }


    /*
        partialDrinks drink objects with the following format :
        {   strDrink: drinkName,
            strDrinkThumb: drinkThumb,
            idDrink : drinkId
        }
        And not the full drink , so we have to fetch full drinks.
    */
    private List<ApiDrink> getActualDrinks(List<ApiDrink> partialDrinks)  {
        List<ApiDrink> actualDrinks = new ArrayList<>();

        for (int i = 0; i < partialDrinks.size(); i++) {
            try {
                actualDrinks.add(fetchDrinkById(partialDrinks.get(i).getIdDrink()).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return actualDrinks;
    }

    @Async
    public CompletableFuture<List<String>> fetchDrinkIngredients(){
        List<String> ingredients = new ArrayList<>();
        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";
        String response = this.template.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("drinks");
        for (int i = 0; i < jsonArray.length(); i++) {
            ingredients.add(jsonArray.getJSONObject(i).getString("strIngredient1"));
        }

        return CompletableFuture.completedFuture(ingredients);
    }

    @Async
    public CompletableFuture<List<String>> fetchDrinkCategories(){
        List<String> categoryNames = new ArrayList<>();
        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list";
        String response = this.template.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("drinks");
        for (int i = 0; i < jsonArray.length(); i++) {
            categoryNames.add(jsonArray.getJSONObject(i).getString("strCategory"));
        }

        return CompletableFuture.completedFuture(categoryNames);
    }

    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksListByCategory(String categoryName){
        this.logger.info("Fetching drinks with category : " + categoryName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=%s", categoryName);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksListByIngredient(String ingredientName){
        this.logger.info("Fetching drinks with ingredient : " + ingredientName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=%s", ingredientName);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    @Async
    public CompletableFuture<ApiDrink> fetchDrinkById(String id){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=%s", id);
        ApiDrink.DrinkList cocktailList = this.template.getForObject(url, ApiDrink.DrinkList.class);
        ApiDrink cocktail = null;

        if (cocktailList.drinks != null){
            cocktail = cocktailList.drinks.get(0);
        }

        return CompletableFuture.completedFuture(cocktail);
    }

    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinkByName(String drinkName){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=%s", drinkName);
        ApiDrink.DrinkList cocktailList = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> cocktail = cocktailList.drinks;

        return CompletableFuture.completedFuture(cocktail);
    }

    @Async
    public CompletableFuture<ApiDrink> fetchRandomDrink(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/random.php";
        logger.info(url);
        ApiDrink.DrinkList randomDrink = this.template.getForObject(url, ApiDrink.DrinkList.class);
        logger.info(randomDrink.drinks.toString());
        return CompletableFuture.completedFuture(randomDrink.drinks.get(0));
    }

    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksByAlcoholic(String alcoholFilter){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=%s", alcoholFilter);
        logger.info(url);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    @Async
    public CompletableFuture<List<ApiDrink>> fetchAllDrinks(){
        List<ApiDrink> apiDrinks = new ArrayList<>();
        try {
            List<String> categories = fetchDrinkCategories().get();
            for (String ingredient : categories) {
                List<ApiDrink> drinkList = fetchDrinksListByCategory(ingredient).get();
                for (ApiDrink drink : drinkList){
                    apiDrinks.add(drink);
                }
            }

            return CompletableFuture.completedFuture(apiDrinks);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }
}
