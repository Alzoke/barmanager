package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.DrinkAssembler;
import com.example.barmanager.backend.assemblers.DrinkDTOAssembler;
import com.example.barmanager.backend.models.Drink;
import com.example.barmanager.backend.models.DrinkDTO;
import com.example.barmanager.backend.repositories.DrinksRepo;
import com.example.barmanager.backend.service.CocktailDBAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.ImageProducer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class DrinkController {
    private final Logger logger;
    private final DrinksRepo drinksRepo;
    private final CocktailDBAPIService drinkService;
    private final DrinkAssembler drinkAssembler;
    private final DrinkDTOAssembler drinkDTOAssembler;

    public DrinkController(DrinksRepo drinksRepo, CocktailDBAPIService drinkService, DrinkAssembler drinkAssembler, DrinkDTOAssembler drinkDTOAssembler) {
        this.drinksRepo = drinksRepo;
        this.drinkService = drinkService;
        this.drinkAssembler = drinkAssembler;
        this.drinkDTOAssembler = drinkDTOAssembler;
        this.logger = LoggerFactory.getLogger(DrinkController.class);
    }

    @GetMapping("/drinks/{id}")
    public ResponseEntity<EntityModel<DrinkDTO>> getDrink(@PathVariable String id) {
        return drinksRepo.findById(id)
                .map(DrinkDTO::new)
                .map(EntityModel::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/drinks")
    public ResponseEntity<CollectionModel<EntityModel<Drink>>> getAllDrinks(){
        return ResponseEntity.ok(drinkAssembler.toCollectionModel(drinksRepo.findAll()));
    }

    @GetMapping("/drinks/all")
    public ResponseEntity<CollectionModel<EntityModel<DrinkDTO>>> getAllDtoDrinks()
    {
        return ResponseEntity.ok(drinkDTOAssembler.toCollectionModel(
                StreamSupport.stream(drinksRepo.findAll().spliterator(),false)
                        .map(DrinkDTO::new).collect(Collectors.toList())));

    }
}
