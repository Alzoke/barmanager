//package com.example.barmanager.backend.controllers;
//
//import com.example.barmanager.backend.assemblers.DrinkDTOAssembler;
//import com.example.barmanager.backend.exceptions.DrinkNotFoundException;
//import com.example.barmanager.backend.models.ApiDrink;
//import com.example.barmanager.backend.models.DrinkDTO;
//import com.example.barmanager.backend.service.CocktailDBAPIService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.hateoas.CollectionModel;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//
//@RestController
//public class DrinkSupplierController {
//    private final Logger logger;
//    private final CocktailDBAPIService drinkService;
//    //private final DrinkAssembler drinkAssembler;
//    private final DrinkDTOAssembler drinkDTOAssembler;
//
//    public DrinkSupplierController(CocktailDBAPIService drinkService, DrinkDTOAssembler drinkDTOAssembler) {
//        this.drinkService = drinkService;
//        this.drinkDTOAssembler = drinkDTOAssembler;
//        this.logger = LoggerFactory.getLogger(DrinkSupplierController.class);
//    }
//
//    /**
//     * The method retrieves the drink from the CocktailDBAPI via DrinkService ,
//     * if the retrieval from the service fails ,the method retrieves the drink from the DrinkRepository
//     * @param id
//     * @return DrinkDTO corresponding to the Drink with the requested ID.
//     */
//    @GetMapping("/supplier/{id}")
//    public ResponseEntity<?> getDrink(@PathVariable String id) {
//        CompletableFuture<ApiDrink> drinkCompletableFuture = drinkService.fetchDrinkById(id);
//        try {
//            ApiDrink drink = drinkCompletableFuture.get();
//            if (drink == null){
//                throw new DrinkNotFoundException(id);
//            }
//            return ResponseEntity.ok(drinkDTOAssembler.toModel(new DrinkDTO(drink)));
//        } catch (InterruptedException | ExecutionException | DrinkNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @GetMapping("/supplier/category")
//    public ResponseEntity<?> getAllDrinksByCategory(@RequestParam String category){
//        CompletableFuture<List<ApiDrink>> drinksFuture = drinkService.fetchDrinksListByCategory(category);
//        try {
//            List<ApiDrink> drinks = drinksFuture.get();
//
//            if (drinks == null){
//                drinks = new ArrayList<>();
//            }
//            return ResponseEntity.ok(drinkDTOAssembler.toCollectionModel(drinks.stream()
//                            .map(DrinkDTO::new)
//                            .collect(Collectors.toList())));
//
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
////    @GetMapping("/supplier")
////    public ResponseEntity<CollectionModel<EntityModel<DrinkDTO>>> getAllDtoDrinks()
////    {
////        return ResponseEntity.ok(drinkDTOAssembler.toCollectionModel(
////                StreamSupport.stream(drinksRepo.findAll().spliterator(),false)
////                        .map(DrinkDTO::new).collect(Collectors.toList())));
////
////    }
//
//}
