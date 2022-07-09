package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.CustomerAssembler;
import com.example.barmanager.backend.assemblers.CustomerDtoAssembler;
import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.CustomerDto;
import com.example.barmanager.backend.repositories.ICustomOrderRepository;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public CustomerController(CustomerAssembler customerAssembler, CustomerDtoAssembler customerDtoAssembler, ICustomerRepository customerRepository, ICustomOrderRepository customOrderRepository)
    {
        this.customerAssembler = customerAssembler;
        this.customerDtoAssembler = customerDtoAssembler;
        this.customerRepository = customerRepository;
        this.customOrderRepository = customOrderRepository;
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

    //TODO: @Delete
    // TODO: @Put


}
