package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.CustomerAssembler;
import com.example.barmanager.backend.assemblers.CustomerDtoAssembler;
import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.CustomerDto;
import com.example.barmanager.backend.repositories.ICustomOrderRepository;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import com.example.barmanager.backend.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * class which represents Customer Controller;
 */
@RestController
public class CustomerController
{
    private final CustomerAssembler customerAssembler;
    private final CustomerDtoAssembler customerDtoAssembler;
    private final ICustomerRepository customerRepository;
    private final ICustomOrderRepository customOrderRepository;
    private final CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    public CustomerController(CustomerAssembler customerAssembler,
                              CustomerDtoAssembler customerDtoAssembler,
                              ICustomerRepository customerRepository,
                              ICustomOrderRepository customOrderRepository,
                              CustomerService customerService)
    {
        this.customerAssembler = customerAssembler;
        this.customerDtoAssembler = customerDtoAssembler;
        this.customerRepository = customerRepository;
        this.customOrderRepository = customOrderRepository;
        this.customerService = customerService;
    }

    /**
     * function that handle Get request for get all exiting customers
     * @return Collection model of entities model of customers with status code "ok"
     */
    @GetMapping("/customers")
    public ResponseEntity<CollectionModel<EntityModel<Customer>>> getAllCustomers()
    {
        return ResponseEntity.ok(customerAssembler.toCollectionModel(customerRepository.findAll()));
    }

    /**
     * function which handle GET request and  receiving single customer by id in the DB
     * @param id  - represents id of the requested customer in DB
     * @return return Entity model of requested customer
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<EntityModel<Customer>> getCustomer(@PathVariable String id)
    {
        return customerRepository.findById(id)
                .map(customerAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));

    }

    /**
     * function that handle Post request for creating new  customer
     * @param newCustomer new customer to create and save into db
     * @return created customer as entity model
     */
    @PostMapping("/customers")
    ResponseEntity<EntityModel<Customer>> createCustomer(@RequestBody Customer newCustomer)
    {
        logger.info("received Customer:"  + newCustomer);
        // initialization orderId field because
        // we get a customer  without orders at first
        newCustomer.setOrdersIds(new ArrayList<>());
        Customer savedCustomer = customerRepository.save(newCustomer);
        return ResponseEntity.created(linkTo(methodOn(CustomerController.class)
                        .getCustomer(savedCustomer.getCustomerId())).toUri())
                .body(customerAssembler.toModel(savedCustomer));
    }

    /**
     * function that handle Get request for get all exiting customers (as DTOs)
     * @return Collection model of entities model of Dtos customers with status code "ok"
     */
    @GetMapping("/customers/info")
    public ResponseEntity<CollectionModel<EntityModel<CustomerDto>>> getCustomersDtos()
    {
        return ResponseEntity.ok(
                customerDtoAssembler.toCollectionModel(
                        StreamSupport.stream(customerRepository.findAll().spliterator(),
                                        false)
                                .map(CustomerDto::new)
                                .collect(Collectors.toList())));
    }

    /**
     * function which handle GET  request and get single DTO Customer
     * @param id  - represents id of the requested customer in DB
     * @return return Entity model of requested customer as DTO
     */
    @GetMapping("/customers/{id}/info")
    public ResponseEntity<EntityModel<CustomerDto>> getCustomerDto(@PathVariable String id)
    {
        return customerRepository.findById(id)
                .map(CustomerDto::new)
                .map(customerDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    /**
     * function that handle DELETE request for removing customer
     * @param id of customer to delete
     * @return deleted customer
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id)
    {
        Customer deletedCustomer = customerService.deleteCustomer(id);
        EntityModel<Customer> deletedCustomerEntityModel = customerAssembler.toModel(deletedCustomer);

        return ResponseEntity.ok(deletedCustomerEntityModel);
    }

    /**
     * function that handle Put req for updating customer
     * @param newCustomer new updated customer,
     * @param id of customer to update
     * @return updated customer as entity model with 'ok' status code
     */
    @PutMapping ("/customers/{id}")
    ResponseEntity<EntityModel<Customer>> updatedCustomer(@RequestBody Customer newCustomer,
                                                          @PathVariable String id)
    {
        return  customerRepository.findById(id)
                .map(customer -> {
                    customer.setIdNumber(newCustomer.getIdNumber());
                    customer.setOrdersIds(newCustomer.getOrdersIds());
                    customer.setFirstName(newCustomer.getFirstName());
                    customer.setLastName(newCustomer.getLastName());
                    return customerRepository.save(customer);
                })
                .map(customerAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

}
