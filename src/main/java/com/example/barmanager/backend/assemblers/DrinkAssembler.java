package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.DrinkController;
import com.example.barmanager.backend.models.Drink;
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
public class DrinkAssembler implements RepresentationModelAssembler<Drink, EntityModel<Drink>> {
    @Override
    public EntityModel<Drink> toModel(Drink entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(DrinkController.class).getDrink(entity.getIdDrink())).withSelfRel(),
                linkTo(methodOn(DrinkController.class).getAllDrinks()).withRel("all products"));
    }

    @Override
    public CollectionModel<EntityModel<Drink>> toCollectionModel(Iterable<? extends Drink> entities) {
        List<Drink> products = (List<Drink>) entities;
        List<EntityModel<Drink>> entityProducts = products.stream().map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(DrinkController.class).getAllDrinks()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
