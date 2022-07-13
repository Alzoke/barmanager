package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.BrunchesController;
import com.example.barmanager.backend.models.BrunchDto;
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
public class BrunchDtoAssembler implements RepresentationModelAssembler<BrunchDto, EntityModel<BrunchDto>>
{
    @Override
    public EntityModel<BrunchDto> toModel(BrunchDto brunchDto)
    {
        return EntityModel.of(brunchDto,
                linkTo(methodOn(BrunchesController.class)
                    .getBrunchDto(brunchDto.getBrunch().getId())).withSelfRel(),
                linkTo(methodOn(BrunchesController.class).getAllBrunchesDto()).withRel("all brunches"));
    }

    @Override
    public CollectionModel<EntityModel<BrunchDto>> toCollectionModel(Iterable<? extends BrunchDto> entities)
    {
        List<BrunchDto> brunchDtos = (List<BrunchDto>) entities;
        List<EntityModel<BrunchDto>> brunchesModels =
                brunchDtos.stream().map(brunchDto -> this.toModel(brunchDto))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(BrunchesController.class).getAllBrunchesDto()).withSelfRel();
        return CollectionModel.of(brunchesModels,link);
    }
}
