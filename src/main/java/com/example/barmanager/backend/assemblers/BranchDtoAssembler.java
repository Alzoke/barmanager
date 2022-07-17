package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.BranchesController;
import com.example.barmanager.backend.models.BranchDto;
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
public class BranchDtoAssembler implements RepresentationModelAssembler<BranchDto, EntityModel<BranchDto>>
{
    @Override
    public EntityModel<BranchDto> toModel(BranchDto brunchDto)
    {
        return EntityModel.of(brunchDto,
                linkTo(methodOn(BranchesController.class)
                    .getBrunchDto(brunchDto.getBranch().getId())).withSelfRel(),
                linkTo(methodOn(BranchesController.class).getAllBrunchesDto()).withRel("all branches"));
    }

    @Override
    public CollectionModel<EntityModel<BranchDto>> toCollectionModel(Iterable<? extends BranchDto> entities)
    {
        List<BranchDto> brunchDtos = (List<BranchDto>) entities;
        List<EntityModel<BranchDto>> brunchesModels =
                brunchDtos.stream().map(brunchDto -> this.toModel(brunchDto))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(BranchesController.class).getAllBrunchesDto()).withSelfRel();
        return CollectionModel.of(brunchesModels,link);
    }
}
