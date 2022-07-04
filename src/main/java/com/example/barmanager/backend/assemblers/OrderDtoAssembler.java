package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.CustomerController;
import com.example.barmanager.backend.controllers.OrderController;
import com.example.barmanager.backend.models.OrderDto;
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
public class OrderDtoAssembler implements RepresentationModelAssembler<OrderDto, EntityModel<OrderDto>>
{
    @Override
    public EntityModel<OrderDto> toModel(OrderDto orderDto)
    {
        return EntityModel.of(orderDto,
                linkTo(methodOn(OrderController.class).getOrderDto(orderDto.getOrder()
                                .getOrderId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getOrdersDtos()).withRel("all orders"));    }

    @Override
    public CollectionModel<EntityModel<OrderDto>> toCollectionModel(Iterable<? extends OrderDto> entities)
    {
        List<OrderDto> orderDtos = (List<OrderDto>) entities;
        List<EntityModel<OrderDto>> orders =
                orderDtos.stream().map(orderDto -> this.toModel(orderDto))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(OrderController.class).getOrdersDtos()).withSelfRel();
        return CollectionModel.of(orders,link);
    }
}
