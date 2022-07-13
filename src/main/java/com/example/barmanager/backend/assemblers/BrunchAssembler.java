package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.BrunchesController;
import com.example.barmanager.backend.controllers.CustomerController;
import com.example.barmanager.backend.models.Brunch;
import com.example.barmanager.backend.models.Customer;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Component
public class BrunchAssembler implements RepresentationModelAssembler<Brunch, EntityModel<Brunch>>
{
    @Override
    public EntityModel<Brunch> toModel(Brunch brunch)
    {
        return EntityModel.of(brunch,
                WebMvcLinkBuilder.linkTo(methodOn(BrunchesController.class).getAllBrunches())
                        .withRel("All brunches"),
                linkTo(methodOn(BrunchesController.class).getBrunch(brunch.getId()))
                        .withSelfRel());    }

    @Override
    public CollectionModel<EntityModel<Brunch>> toCollectionModel(Iterable<? extends Brunch> entities)
    {
        List<Brunch> brunches = (List<Brunch>) entities;
        List<EntityModel<Brunch>> entityProducts = brunches.stream()
                .map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(BrunchesController.class).getAllBrunches()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
