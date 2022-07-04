package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.CustomerController;
import com.example.barmanager.backend.models.CustomerDto;
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
public class CustomerDtoAssembler implements RepresentationModelAssembler<CustomerDto, EntityModel<CustomerDto>>
{

    @Override
    public EntityModel<CustomerDto> toModel(CustomerDto customerDto)
    {
        return EntityModel.of(customerDto,
        linkTo(methodOn(CustomerController.class).getCustomerDto(customerDto.getCustomer()
                .getCustomerId())).withSelfRel(),
        linkTo(methodOn(CustomerController.class).getCustomersDtos()).withRel("all customers"));
    }

    @Override
    public CollectionModel<EntityModel<CustomerDto>> toCollectionModel(Iterable<? extends CustomerDto> entities)
    {
        List<CustomerDto> customerDtos = (List<CustomerDto>) entities;
        List<EntityModel<CustomerDto>> customersEntities =
                customerDtos.stream().map(customerDto -> this.toModel(customerDto))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(CustomerController.class).getCustomersDtos()).withSelfRel();
        return CollectionModel.of(customersEntities,link);
    }
}
