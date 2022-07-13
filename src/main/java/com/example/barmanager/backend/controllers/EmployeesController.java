package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.EmployeeAssembler;
import com.example.barmanager.backend.exceptions.BrunchNotFoundException;
import com.example.barmanager.backend.exceptions.EmployeeNotFoundException;
import com.example.barmanager.backend.models.BrunchDto;
import com.example.barmanager.backend.models.Employee;
import com.example.barmanager.backend.repositories.IEmployeeRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class EmployeesController
{
    private final EmployeeAssembler employeeAssembler;
    private final EmployeeDtoAssembler employeeDtoAssembler;
    private final IEmployeeRepository employeeRepository;

    public EmployeesController(EmployeeAssembler employeeAssembler, EmployeeDtoAssembler employeeDtoAssembler, IEmployeeRepository employeeRepository)
    {
        this.employeeAssembler = employeeAssembler;
        this.employeeDtoAssembler = employeeDtoAssembler;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public ResponseEntity<CollectionModel<EntityModel<Employee>>> getAllEmployees()
    {
        return ResponseEntity.ok(employeeAssembler.toCollectionModel(employeeRepository.findAll()));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> getEmployee(@PathVariable String id)
    {
        return employeeRepository.findById(id)
                .map(employee -> employeeAssembler.toModel(employee)).map(ResponseEntity::ok)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @GetMapping("/employees/info")
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDto>>> getAllDtoEmployees()
    {
        return ResponseEntity.ok(
                employeeDtoAssembler.toCollectionModel(
                        StreamSupport.stream(employeeRepository.findAll().spliterator(),
                                        false)
                                .map(EmployeeDto::new)
                                .collect(Collectors.toList())));    }

    @GetMapping("/employees/{id}/info")
    public ResponseEntity<EntityModel<EmployeeDto>> getEmployeeDto(@PathVariable String id)
    {
        return employeeRepository.findById(id)
                .map(EmployeeDto::new)
                .map(employeeDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BrunchNotFoundException(id));
    }




}
