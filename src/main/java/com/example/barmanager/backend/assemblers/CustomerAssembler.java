package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.CustomerController;
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
public class CustomerAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>>
{

    @Override
    public EntityModel<Customer> toModel(Customer customer)
    {
        return EntityModel.of(customer,
                WebMvcLinkBuilder.linkTo(methodOn(CustomerController.class).getAllCustomers())
                        .withRel("All customers"),
                linkTo(methodOn(CustomerController.class).getCustomer(customer.getCustomerId()))
                        .withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<Customer>> toCollectionModel(Iterable<? extends Customer> entities)
    {
        List<Customer> customers = (List<Customer>) entities;
        List<EntityModel<Customer>> entityProducts = customers.stream()
                .map(this::toModel).collect(Collectors.toList());

        Link link = linkTo(methodOn(CustomerController.class).getAllCustomers()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
