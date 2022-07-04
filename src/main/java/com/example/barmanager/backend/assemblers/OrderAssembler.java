package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.OrderController;
import com.example.barmanager.backend.models.Order;
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
public class OrderAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>>
{
    @Override
    public EntityModel<Order> toModel(Order order)
    {
        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrder(order.getOrderId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getOrders()).withRel("All orders"));
    }

    @Override
    public CollectionModel<EntityModel<Order>> toCollectionModel(Iterable<? extends Order> entities)
    {
        List<Order> orders = (List<Order>) entities;

        List<EntityModel<Order>> entityProducts = orders.stream().map(this::toModel)
                .collect(Collectors.toList());

        Link link = linkTo(methodOn(OrderController.class).getOrders()).withSelfRel();
        return CollectionModel.of(entityProducts, link);
    }
}
