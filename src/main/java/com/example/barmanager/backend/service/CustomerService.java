package com.example.barmanager.backend.service;

import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.repositories.ICustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .orElseThrow(() -> new CustomerNotFoundException(idNumber + "Id number"));

        return customer;
    }

    /**
     * function that delete customer iff he exists
     * @return the deleted customer
     */
    public Customer deleteCustomer(String customerId)
    {
        Customer deletedCustomer = null;

        if ( customerId != null )
        {
            deletedCustomer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException(customerId));

            customerRepository.deleteById(customerId);
        }

        return deletedCustomer;
    }

}
