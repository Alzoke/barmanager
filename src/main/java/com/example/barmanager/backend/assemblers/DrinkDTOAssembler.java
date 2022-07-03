package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.DrinkController;
import com.example.barmanager.backend.models.Drink;
import com.example.barmanager.backend.models.DrinkDTO;
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
public class DrinkDTOAssembler implements RepresentationModelAssembler<DrinkDTO, EntityModel<DrinkDTO>>
{

    @Override
    public EntityModel<DrinkDTO> toModel(DrinkDTO entity)
    {
        return EntityModel.of(entity,
                linkTo(methodOn(DrinkController.class).getDrink(entity.getDrink().getIdDrink())).withSelfRel(),
                linkTo(methodOn(DrinkController.class).getAllDrinks()).withRel("all products"));    }

    @Override
    public CollectionModel<EntityModel<DrinkDTO>> toCollectionModel(Iterable<? extends DrinkDTO> entities)
    {
        List<DrinkDTO> drinkDTOS = (List<DrinkDTO>) entities;
        List<EntityModel<DrinkDTO>> drinksEntities =
                drinkDTOS.stream().map(drinkDTO -> this.toModel(drinkDTO))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(DrinkController.class).getAllDtoDrinks()).withSelfRel();
        return CollectionModel.of(drinksEntities,link);
    }
}
