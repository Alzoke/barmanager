package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICustomerRepository extends MongoRepository<Customer, String> {
}
