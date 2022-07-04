package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.DrinkSupplierController;
import com.example.barmanager.backend.models.ApiDrink;
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
public class ApiDrinkAssembler implements RepresentationModelAssembler<ApiDrink, EntityModel<ApiDrink>> {
    @Override
    public EntityModel<ApiDrink> toModel(ApiDrink entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(DrinkSupplierController.class).getDrink(entity.getIdDrink())).withSelfRel(),
                linkTo(methodOn(DrinkSupplierController.class).getAllDrinks()).withRel("all products"));
    }

    @Override
    public CollectionModel<EntityModel<ApiDrink>> toCollectionModel(Iterable<? extends ApiDrink> entities) {
        List<ApiDrink> products = (List<ApiDrink>) entities;
        List<EntityModel<ApiDrink>> entityProducts = products.stream().map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(DrinkSupplierController.class).getAllDrinks()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
