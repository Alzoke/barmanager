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

    @GetMapping("/customers")
    public ResponseEntity<CollectionModel<EntityModel<Customer>>> getAllCustomers()
    {
        return ResponseEntity.ok(customerAssembler.toCollectionModel(customerRepository.findAll()));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<EntityModel<Customer>> getCustomer(@PathVariable String id)
    {
        return customerRepository.findById(id)
                .map(customerAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));

    }

    @PostMapping("/customers")
    ResponseEntity<EntityModel<Customer>> createCustomer(@RequestBody Customer newCustomer)
    {
        logger.info("received Customer:"  + newCustomer);
        // init orderId field becasue we get an customer item from front side without orders at first
        newCustomer.setOrdersIds(new ArrayList<>());
        Customer savedCustomer = customerRepository.save(newCustomer);
        return ResponseEntity.created(linkTo(methodOn(CustomerController.class)
                        .getCustomer(savedCustomer.getCustomerId())).toUri())
                .body(customerAssembler.toModel(savedCustomer));
    }

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

    @GetMapping("/customers/{id}/info")
    public ResponseEntity<EntityModel<CustomerDto>> getCustomerDto(@PathVariable String id)
    {
        return customerRepository.findById(id)
                .map(CustomerDto::new)
                .map(customerDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id)
    {
        Customer deletedCustomer = customerService.deleteCustomer(id);
        EntityModel<Customer> deletedCustomerEntityModel = customerAssembler.toModel(deletedCustomer);

        return ResponseEntity.ok(deletedCustomerEntityModel);
    }

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
