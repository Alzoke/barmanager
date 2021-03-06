package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.OrderAssembler;
import com.example.barmanager.backend.assemblers.OrderDtoAssembler;
import com.example.barmanager.backend.exceptions.OrderNotFoundException;
import com.example.barmanager.backend.models.*;
import com.example.barmanager.backend.repositories.*;
import com.example.barmanager.backend.service.CustomerService;
import com.example.barmanager.backend.service.OrderService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller which is responsible for managing
 * and routing http requests for the orders route
 */
@RestController
public class OrderController {
    private final IOrderRepository orderRepository;
    private final OrderAssembler orderAssembler;
    private final CustomOrderRepository customOrderRepository;
    private final ICustomerRepository customerRepository;
    private final OrderDtoAssembler orderDtoAssembler;
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomBranchRepository customBrunchRepository;

    public OrderController(IOrderRepository orderRepository,
                           OrderAssembler orderAssembler,
                           CustomOrderRepository customOrderRepository,
                           ICustomerRepository customerRepository,
                           OrderDtoAssembler orderDtoAssembler, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderAssembler = orderAssembler;
        this.customOrderRepository = customOrderRepository;
        this.customerRepository = customerRepository;
        this.orderDtoAssembler = orderDtoAssembler;
        this.orderService = orderService;
    }

    /**
     * function which handle get request and return single order by id
     *
     * @param id - represents id of the requested order in DB
     * @return return Entity model of requested order
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> getOrder(@PathVariable String id) {
        Optional<Order> byId = orderRepository.findById(id);
        logger.info(String.valueOf(byId.get().getOrderedDrinks().size()));
        return orderRepository.findById(id)
                .map(orderAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * function that handle Get request for get all exiting orders
     *
     * @return Collection model of entities model of order with status code "ok"
     */
    @GetMapping("/orders")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> getOrders() {
        return ResponseEntity.ok(orderAssembler
                .toCollectionModel(orderRepository.findAll()));
    }

    /**
     * function which handle post request for saving new order into DB
     *
     * @param newOrder order need to be saved insert into db
     * @return created order
     */
    @PostMapping("/orders")
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order newOrder) {
        Customer customer = customerService.findCustomerByIdNumber(newOrder.getCustomer().getIdNumber());
        newOrder.setCustomer(customer);
        Order savedOrder = customOrderRepository.saveNewOrder(newOrder);
        logger.info("saved order: " + savedOrder);

        return ResponseEntity.created(linkTo(methodOn(OrderController.class)
                        .getOrder(savedOrder.getOrderId())).toUri())
                .body(orderAssembler.toModel(savedOrder));
    }

    /**
     * function that handle Get request for get all exiting orders (as DTOs)
     *
     * @return Collection model of entities model of Dto's orders with status code "ok"
     */
    @GetMapping("/orders/info")
    public ResponseEntity<CollectionModel<EntityModel<OrderDto>>> getOrdersDtos() {
        return ResponseEntity.ok(
                orderDtoAssembler.toCollectionModel(
                        StreamSupport.stream(orderRepository.findAll().spliterator(),
                                        false)
                                .map(OrderDto::new)
                                .collect(Collectors.toList())));
    }

    @GetMapping("/orders/openOrders")
    public ResponseEntity<CollectionModel<EntityModel<OrderDto>>> getOpenOrders() {
        List<Order> orders = orderService.getOpenOrder();
        logger.info(orders.toString());
        return ResponseEntity.ok(
                orderDtoAssembler.toCollectionModel(
                        StreamSupport.stream(orders
                                                .spliterator(),
                                        false)
                                .map(OrderDto::new)
                                .collect(Collectors.toList())));
    }

    /**
     * function which handle get request get single DTO order
     *
     * @param id - represents id of the requested order in DB
     * @return return Entity model of requested order as DTO
     */
    @GetMapping("/orders/{id}/info")
    public ResponseEntity<EntityModel<OrderDto>> getOrderDto(@PathVariable String id) {
        return orderRepository.findById(id)
                .map(OrderDto::new)
                .map(orderDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * GET handler for get request of the 10 most popular ordered drinks
     *
     * @return ResponseEntity.ok ,the response's body contains a List of documents
     *               when each document contains a drink name, and its count.
     */
    @GetMapping("/orders/drinkPopularity")
    public ResponseEntity<List<Document>> getDrinkPopularity() {
        return ResponseEntity.ok(customOrderRepository.getTenMostOrderedDrinks());
    }

    /**
     * Handler for GET request for getting annual profit by year
     *
     * @param year requested year
     * @return ResponseEntity.ok ,the response's body contains a List of documents
     *                    when each document contains month, and its total profit.
     */
    @GetMapping("/orders/profits")
    public ResponseEntity<List<Document>> getProfits(@RequestParam int year) {
        return ResponseEntity.ok(customOrderRepository.getProfitsByYear(year));
    }

    /**
     * GET handler for getting orders in range of 2 dates
     *
     * @param sDate start date
     * @param eDate end date
     * @return collection model of entity model of Orders with
     */
    @GetMapping("/orders/filterByOrderDate")
    public ResponseEntity<CollectionModel<EntityModel<OrderDto>>>
    filterByDateRange(@RequestParam Optional<String> sDate, @RequestParam Optional<String> eDate) {
        String startDate = sDate.orElseGet(() -> String.valueOf(LocalDate.now()));
        // minusDays(1) -> because LocalDate return day starting in 1
        String endDate = eDate.orElseGet(() -> String.valueOf(LocalDate.now().minusDays(1)));

        List<OrderDto> orderBetweenDates = orderService.getOrderBetweenDates(startDate, endDate);
        logger.info("orderBetweenDates" + orderBetweenDates);

        return ResponseEntity.ok(orderDtoAssembler
                .toCollectionModel(orderBetweenDates));
    }

    /**
     * Get handler for getting an (open or close) order (if exists) that belongs to given seat number
     *
     * @param seatNumber  requests seat number
     * @param orderStatus
     * @return ResponseEntity.OK and an Entity Model of fitting orderDto
     */
    @GetMapping("/orders/closeBySeat")
    public ResponseEntity<EntityModel<OrderDto>> findByStatusAndBySeat(
            @RequestParam int seatNumber, eOrderStatus orderStatus) {
        ResponseEntity<EntityModel<OrderDto>> entity =
                customOrderRepository.findCloseBySeat(seatNumber)
                        .map(OrderDto::new).map(orderDtoAssembler::toModel)
                        .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

        return entity;
    }

    /**
     * Handles Put request for updating order status to - close,
     *
     * @param id of requested order
     * @return ResponseEntity.created and an Entity Model of the updated order,
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<?> setOrderStatusToClose(@PathVariable String id) {
        Order orderToUpdate = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        Order order = customOrderRepository.closeOrder(orderToUpdate);

        EntityModel<Order> orderEntityModel = orderAssembler.toModel(order);

        return ResponseEntity.created(orderEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(orderEntityModel);
    }

    /**
     * handles Delete request for deleting order from DB
     *
     * @param id of requested order
     * @return ResponseEntity with status code - ok if no error occurred,
     * or ResponseEntity with status code -bad request
     * wraps entity model of the deleted order.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> deleteOrder(@PathVariable String id) {

        Order orderToDelete = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        boolean isSucceeded = customOrderRepository.deleteOrder(orderToDelete);
        EntityModel<Order> orderEntityModel = orderAssembler.toModel(orderToDelete);

        if (isSucceeded)
            return ResponseEntity.ok(orderEntityModel);
        else
            return ResponseEntity.badRequest().body(orderEntityModel);
    }
}
