package com.example.barmanager.backend.service;

import com.example.barmanager.backend.exceptions.BranchNotFoundException;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Employee;
import com.example.barmanager.backend.repositories.CustomBrunchRepository;
import com.example.barmanager.backend.repositories.IBrunchRepository;
import com.example.barmanager.backend.repositories.IEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService
{
    private final CustomBrunchRepository customBrunchRepository;
    private final IBrunchRepository brunchRepository;
    private final IEmployeeRepository employeeRepository;

    public EmployeeService(CustomBrunchRepository customBrunchRepository, IBrunchRepository brunchRepository, IEmployeeRepository employeeRepository)
    {
        this.customBrunchRepository = customBrunchRepository;
        this.brunchRepository = brunchRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * function for validation of new employee
     * @param employee to validate
     * @return boolean (valid or not)
     * @throws IllegalArgumentException if name is empty
     * @throws NullPointerException if name is null
     */
    public boolean validationOfNewEmployee(Employee employee) throws IllegalArgumentException,
            NullPointerException
    {
        if ( employee == null )
        {
            throw new NullPointerException();
        }
        if ( employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() )
        {
            throw new IllegalArgumentException("name can not be empty");
        }
        return true;
    }

    /**
     * function that creates new employee
     * @param newEmployee
     * @param branchId
     * @return optional of created new employee
     */
    public Optional<Employee> createNewEmployee(Employee newEmployee, String branchId)
    {
        Employee savedEmployee = null;

        Branch branch = brunchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));

        if ( validationOfNewEmployee(newEmployee) )
        {
            savedEmployee = employeeRepository.save(newEmployee);
            customBrunchRepository.addEmployee(branch,savedEmployee);
        }

        return Optional.ofNullable(savedEmployee);

    }

}
