package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IEmployeeRepository extends MongoRepository<Employee,String>
{
}
