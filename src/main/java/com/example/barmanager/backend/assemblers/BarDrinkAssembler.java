package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.InventoryController;
import com.example.barmanager.backend.models.BarDrink;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BarDrinkAssembler implements RepresentationModelAssembler<BarDrink, EntityModel<BarDrink>> {
    @Override
    public EntityModel<BarDrink> toModel(BarDrink entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(InventoryController.class).getBarDrink(entity.getId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).getAllDrinks()).withRel("all bar drinks"));
    }

    @Override
    public CollectionModel<EntityModel<BarDrink>> toCollectionModel(Iterable<? extends BarDrink> entities) {
        List<BarDrink> products = (List<BarDrink>) entities;
        List<EntityModel<BarDrink>> entityProducts = products.stream().map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(InventoryController.class).getAllDrinks()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}