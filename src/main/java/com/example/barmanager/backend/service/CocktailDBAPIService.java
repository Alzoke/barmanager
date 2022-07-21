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

    /**
     * Some responses from CocktailDBAPI returns objects which represent only the drink name , thumb and id.
     * this method receives those objects and fetches a "full" Drinks objects from the API.
     * @param partialDrinks partial drink objects
     * @return List of "full" Drink objects
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

    /**
     * Fetch every possible ingredient name from CocktailDBAPI
     * @return CompletableFuture of the api response which is a list of ingredient names.
     */
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

    /**
     * Fetch every possible category name from CocktailDBAPI
     * @return CompletableFuture of the api response which is a list of category names.
     */
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

    /**
     * Fetch a collection of drinks from CocktailDBAPI filtered by category.
     * @param categoryName the name of the category to filter the drinks by.
     * @return CompletableFuture of a List of Drinks filtered by category.
     */
    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksListByCategory(String categoryName){
        this.logger.info("Fetching drinks with category : " + categoryName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=%s", categoryName);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    /**
     * Fetch a collection of drinks from CocktailDBAPI filtered by ingredient.
     * @param ingredientName the name of the ingredient to filter the drinks by.
     * @return CompletableFuture of a List of Drinks filtered by ingredient.
     */
    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksListByIngredient(String ingredientName){
        this.logger.info("Fetching drinks with ingredient : " + ingredientName);
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=%s", ingredientName);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    /**
     * Fetch drink by id from CocktailDBAPI
     * @param id the id corresponding to the drink.
     * @return CompletableFuture of a Drink.
     */
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

    /**
     * Fetch list of drinks by a name from CocktailDBAPI (the api response includes drinks which contain the requested name in its name).
     * @param drinkName Drink name
     * @return CompletableFuture of a list of Drinks.
     */
    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinkByName(String drinkName){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=%s", drinkName);
        ApiDrink.DrinkList cocktailList = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> cocktail = cocktailList.drinks;

        return CompletableFuture.completedFuture(cocktail);
    }

    /**
     * Fetch a random drink from CocktailDBAPI.
     * @return CompletableFuture of Drink.
     */
    @Async
    public CompletableFuture<ApiDrink> fetchRandomDrink(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/random.php";
        logger.info(url);
        ApiDrink.DrinkList randomDrink = this.template.getForObject(url, ApiDrink.DrinkList.class);
        logger.info(randomDrink.drinks.toString());
        return CompletableFuture.completedFuture(randomDrink.drinks.get(0));
    }

    /**
     * Fetch a collection of drinks from CocktailDBAPI filtered by alcoholic filter.
     * @param alcoholFilter the name of the alcoholic filter to filter the drinks by.
     * @return CompletableFuture of a List of Drinks filtered by alcoholic filter.
     */
    @Async
    public CompletableFuture<List<ApiDrink>> fetchDrinksByAlcoholic(String alcoholFilter){
        String url = String.format("https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=%s", alcoholFilter);
        logger.info(url);
        ApiDrink.DrinkList response = this.template.getForObject(url, ApiDrink.DrinkList.class);
        List<ApiDrink> partialDrinks = new ArrayList(response.drinks);
        List<ApiDrink> drinks = getActualDrinks(partialDrinks);

        return CompletableFuture.completedFuture(drinks);
    }

    /**
     * Fetch every available drink from CocktailDBAPI,
     * the API does not include a request for fetching all the drinks ,
     * so fetchAllDrinks fetches drinks filtered by category for every category available in the API
     * which guarantees all the drinks gets fetched.
     * @return CompletableFuture of a List of Drinks
     */
    @Async
    public CompletableFuture<List<ApiDrink>> fetchAllDrinks(){
        List<ApiDrink> apiDrinks = new ArrayList<>();
        try {
            List<String> categories = fetchDrinkCategories().get();
            for (String category : categories) {
                List<ApiDrink> drinkList = fetchDrinksListByCategory(category).get();
                apiDrinks.addAll(drinkList);
            }

            return CompletableFuture.completedFuture(apiDrinks);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }
}
