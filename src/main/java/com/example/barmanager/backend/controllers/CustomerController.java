package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.CustomerAssembler;
import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CustomerController
{
    private final CustomerAssembler customerAssembler;
    private final ICustomerRepository customerRepository;

    public CustomerController(CustomerAssembler customerAssembler, ICustomerRepository customerRepository)
    {
        this.customerAssembler = customerAssembler;
        this.customerRepository = customerRepository;
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
        return  ResponseEntity.created(linkTo(methodOn(CustomerController.class)
                .getCustomer(savedCustomer.getCustomerId())).toUri())
                .body(customerAssembler.toModel(savedCustomer));
    }

}
