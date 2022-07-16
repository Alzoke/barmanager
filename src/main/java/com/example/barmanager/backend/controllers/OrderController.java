package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.OrderAssembler;
import com.example.barmanager.backend.assemblers.OrderDtoAssembler;
import com.example.barmanager.backend.exceptions.BrunchNotFoundException;
import com.example.barmanager.backend.exceptions.OrderNotFoundException;
import com.example.barmanager.backend.models.*;
import com.example.barmanager.backend.repositories.*;
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
import java.util.Objects;
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
    private final IBrunchRepository brunchRepository;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired  private CustomerService customerService;
    @Autowired
    private CustomBrunchRepository customBrunchRepository;

    public OrderController(IOrderRepository orderRepository,
                           OrderAssembler orderAssembler,
                           CustomOrderRepository customOrderRepository,
                           ICustomerRepository customerRepository,
                           OrderDtoAssembler orderDtoAssembler, IBrunchRepository brunchRepository)
    {
        this.orderRepository = orderRepository;
        this.orderAssembler = orderAssembler;
        this.customOrderRepository = customOrderRepository;
        this.customerRepository = customerRepository;
        this.orderDtoAssembler = orderDtoAssembler;
        this.brunchRepository = brunchRepository;
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
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order newOrder,
                                                @RequestParam Optional<String> branchId)
    {
        Customer customer = customerService.findCustomerByIdNumber(newOrder.getCustomer().getIdNumber());
        Branch branch = null;
        if ( branchId.isPresent() )
        {
             branch = brunchRepository.findById(branchId.get()).orElseThrow(() ->
                    new BrunchNotFoundException(branchId.get()));
        }
//        newOrder.setBranch(branch);
        newOrder.setCustomer(customer);
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

        List<Order> orders = orderRepository.findByOrderStatus(eOrderStatus.Open);
        logger.info(orders.toString());
        return ResponseEntity.ok(
                orderDtoAssembler.toCollectionModel(
                        StreamSupport.stream(orders
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
        return ResponseEntity.ok(customOrderRepository.getTenMostOrderedDrinks());
    }

    @GetMapping("/orders/profits")
    public ResponseEntity<List<Document>> getProfits(@RequestParam int year){
        return ResponseEntity.ok(customOrderRepository.getProfitsByYear(year));
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

    @GetMapping("/orders/closeBySeat")
    public ResponseEntity<EntityModel<OrderDto>> findByStatusAndBySeat(
            @RequestParam int seatNumber, eOrderStatus orderStatus )
    {
        ResponseEntity<EntityModel<OrderDto>> entity =
                customOrderRepository.findCloseBySeat(seatNumber)
//                orderRepository.findByOrderStatusAndSeatNumber(orderStatus, seatNumber)
                .map(OrderDto::new).map(orderDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//        logger.info(String.valueOf(Objects.requireNonNull(entity.getBody()).getContent().getOrderedItems().size()));
        return entity;


    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> setOrderStatusToClose(@PathVariable String id)
    {
        Order orderToUpdate = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        Order order = customOrderRepository.closeOrder(orderToUpdate);

        return ResponseEntity.ok(orderAssembler
                .toModel(order));

    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> deleteOrder(@PathVariable String id){

        Order orderToDelete = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        boolean isSucceeded = customOrderRepository.deleteOrder(orderToDelete);
        EntityModel<Order> orderEntityModel = orderAssembler.toModel(orderToDelete);

        if ( isSucceeded )
            return ResponseEntity.ok(orderEntityModel);
        else return ResponseEntity.badRequest().body(orderEntityModel);



    }
}
