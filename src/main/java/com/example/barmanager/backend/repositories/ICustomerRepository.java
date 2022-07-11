package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ICustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByIdNumber(int idNumber);

}
