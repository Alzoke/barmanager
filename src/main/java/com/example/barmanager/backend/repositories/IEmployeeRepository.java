package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IEmployeeRepository extends MongoRepository<Employee,String>
{
    /**
     * find all employees inside given Branch
     * @param branch
     * @return return fitting employees
     */
    List<Employee> findByBranchesContains(Branch branch);

}
