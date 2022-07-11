package com.example.barmanager.backend.service;

import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import org.springframework.stereotype.Service;
@Service
public class CustomerService
{
    private final ICustomerRepository customerRepository;

    public CustomerService(ICustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomerByIdNumber(int idNumber)
    {
        Customer customer = customerRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new CustomerNotFoundException(String.valueOf(idNumber) + "Id number"));

        return customer;
    }
}
