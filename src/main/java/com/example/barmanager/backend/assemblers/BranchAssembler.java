package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.BranchesController;
import com.example.barmanager.backend.models.Branch;
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
public class BranchAssembler implements RepresentationModelAssembler<Branch, EntityModel<Branch>>
{
    @Override
    public EntityModel<Branch> toModel(Branch brunch)
    {
        return EntityModel.of(brunch,
                WebMvcLinkBuilder.linkTo(methodOn(BranchesController.class).getAllBrunches())
                        .withRel("All branches"),
                linkTo(methodOn(BranchesController.class).getBrunch(brunch.getId()))
                        .withSelfRel());    }

    @Override
    public CollectionModel<EntityModel<Branch>> toCollectionModel(Iterable<? extends Branch> entities)
    {
        List<Branch> brunches = (List<Branch>) entities;
        List<EntityModel<Branch>> entityProducts = brunches.stream()
                .map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(BranchesController.class).getAllBrunches()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
