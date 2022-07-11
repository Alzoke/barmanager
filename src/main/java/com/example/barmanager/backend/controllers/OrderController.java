package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.OrderAssembler;
import com.example.barmanager.backend.assemblers.OrderDtoAssembler;
import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.exceptions.OrderNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.models.OrderDto;
import com.example.barmanager.backend.models.eOrderStatus;
import com.example.barmanager.backend.repositories.CustomOrderRepository;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import com.example.barmanager.backend.repositories.IOrderRepository;
import com.example.barmanager.backend.service.CustomerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController
{
    private final IOrderRepository orderRepository;
    private final OrderAssembler orderAssembler;
    private final CustomOrderRepository customOrderRepository;
    private final ICustomerRepository customerRepository;
    private final OrderDtoAssembler orderDtoAssembler;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired  private CustomerService customerService;

    public OrderController(IOrderRepository orderRepository,
                           OrderAssembler orderAssembler,
                           CustomOrderRepository customOrderRepository,
                           ICustomerRepository customerRepository,
                           OrderDtoAssembler orderDtoAssembler)
    {
        this.orderRepository = orderRepository;
        this.orderAssembler = orderAssembler;
        this.customOrderRepository = customOrderRepository;
        this.customerRepository = customerRepository;
        this.orderDtoAssembler = orderDtoAssembler;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> getOrder(@PathVariable String id)
    {
        Optional<Order> byId = orderRepository.findById(id);
        logger.info(String.valueOf(byId.get().getOrderedDrinks().size()));
        return orderRepository.findById(id)
                .map(order -> orderAssembler.toModel(order)).map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));

    }

    @GetMapping("/orders")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> getOrders()
    {
        return ResponseEntity.ok(orderAssembler
                .toCollectionModel(orderRepository.findAll()));
    }



    @PostMapping("/orders")
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order newOrder)
    {
//        Order savedOrder = orderRepository.save(newOrder);
        Customer optionalCustomer = customerRepository.findByIdNumber
                (newOrder.getCustomer().getIdNumber())
                .orElseThrow(() -> new CustomerNotFoundException(newOrder.getCustomer().getCustomerId()));

        newOrder.setCustomer(optionalCustomer);
        Order savedOrder = customOrderRepository.saveNewOrder(newOrder);
        System.out.println(savedOrder);

        return ResponseEntity.created(linkTo(methodOn(OrderController.class)
                .getOrder(savedOrder.getOrderId())).toUri())
                .body(orderAssembler.toModel(savedOrder));
    }

    @GetMapping("/orders/info")
    public ResponseEntity<CollectionModel<EntityModel<OrderDto>>> getOrdersDtos()
    {
        return ResponseEntity.ok(
                orderDtoAssembler.toCollectionModel(
                        StreamSupport.stream(orderRepository.findAll().spliterator(),
                                        false)
                                .map(OrderDto::new)
                                .collect(Collectors.toList())));
    }

    @GetMapping("/orders/openOrders")
    public ResponseEntity<CollectionModel<EntityModel<OrderDto>>> getOpenOrders()
    {
        return ResponseEntity.ok(
                orderDtoAssembler.toCollectionModel(
                        StreamSupport.stream(orderRepository
                                                .findByOrderStatus(eOrderStatus.Open)
                                                .spliterator(),
                                        false)
                                .map(OrderDto::new)
                                .collect(Collectors.toList())));
    }

    @GetMapping("/orders/{id}/info")
    public ResponseEntity<EntityModel<OrderDto>> getOrderDto(@PathVariable String id)
    {
        return orderRepository.findById(id)
                .map(OrderDto::new)
                .map(orderDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/orders/drinkPopularity")
    public ResponseEntity<List<Document>> getDrinkPopularity(){
        return ResponseEntity.ok(customOrderRepository.getMostOrderedDrinks());
    }
    @GetMapping("/orders/filterByOrderDate")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> filterByDateRange
            (@RequestParam Optional<String> sDate, @RequestParam Optional<String> eDate)
    {
        LocalDate startDate = LocalDate.parse(sDate.orElseGet(() -> String.valueOf(LocalDate.now())));
        LocalDate endDate = LocalDate.parse(eDate.orElseGet(() ->
                        String.valueOf(LocalDate.now().minusDays(1))))
                .plusDays(1);
        return ResponseEntity.ok(orderAssembler
                .toCollectionModel(orderRepository.findByOrderDateBetween(startDate,endDate)));
    }


    @PutMapping("/orders/{id}")
    ResponseEntity<?> setOrderStatusToClose(@RequestBody Order orderToUpdate, @PathVariable String id)
    {
        logger.info("received order : " + orderToUpdate);
        Customer customer = customerService.findCustomerByIdNumber
                (orderToUpdate.getCustomer().getIdNumber());
        Order updatedOrder = orderRepository.findById(id)
                .map(order -> {
                    order.setOrderId(orderToUpdate.getOrderId());
                    order.setOrderDate(orderToUpdate.getOrderDate());
                    order.setOrderStatus(eOrderStatus.Close);
                    order.setSeatNumber(orderToUpdate.getSeatNumber());
                    order.setBill(orderToUpdate.getBill());
                    order.setOrderedDrinks(orderToUpdate.getOrderedDrinks());
                    order.setCustomer(customer);
                    return orderRepository.save(order);
                }).orElseThrow(() -> new OrderNotFoundException(id));

        return ResponseEntity.ok(orderAssembler
                .toModel(updatedOrder));

    }
}
