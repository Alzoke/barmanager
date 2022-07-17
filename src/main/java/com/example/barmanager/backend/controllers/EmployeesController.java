package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.EmployeeAssembler;
import com.example.barmanager.backend.assemblers.EmployeeDtoAssembler;
import com.example.barmanager.backend.exceptions.BranchNotFoundException;
import com.example.barmanager.backend.exceptions.EmployeeNotFoundException;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Employee;
import com.example.barmanager.backend.models.EmployeeDto;
import com.example.barmanager.backend.repositories.CustomBrunchRepository;
import com.example.barmanager.backend.repositories.IBrunchRepository;
import com.example.barmanager.backend.repositories.IEmployeeRepository;
import com.example.barmanager.backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeesController
{
    private final EmployeeAssembler employeeAssembler;
    private final EmployeeDtoAssembler employeeDtoAssembler;
    private final IEmployeeRepository employeeRepository;
    private final IBrunchRepository brunchRepository;
    private final CustomBrunchRepository customBrunchRepository;

    @Autowired private EmployeeService employeeService;

    public EmployeesController(EmployeeAssembler employeeAssembler,
                               EmployeeDtoAssembler employeeDtoAssembler,
                               IEmployeeRepository employeeRepository,
                               IBrunchRepository brunchRepository,
                               CustomBrunchRepository customBrunchRepository)
    {
        this.employeeAssembler = employeeAssembler;
        this.employeeDtoAssembler = employeeDtoAssembler;
        this.employeeRepository = employeeRepository;
        this.brunchRepository = brunchRepository;
        this.customBrunchRepository = customBrunchRepository;
    }

    /**
     * function that handle Post request for creating new  employee
     * @param newEmployee new employee to save into db
     * @param branchId the brunch id that new employee should be added
     * @return created employee
     */
    @PostMapping("/employees")
    ResponseEntity<EntityModel<Employee>> createEmployee(@RequestBody Employee newEmployee,
                                                         @RequestParam String branchId)
    {
        newEmployee.setBranches(new ArrayList<>());

        /*Branch branch = brunchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));
        Employee savedEmployee = employeeRepository.save(newEmployee);

        customBrunchRepository.addEmployee(branch,savedEmployee);*/
        Employee savedEmployee = employeeService.createNewEmployee(newEmployee, branchId)
                .orElseThrow(() -> new RuntimeException("Error while saving new employee"));

        return ResponseEntity.created(linkTo(methodOn(EmployeesController.class)
                        .getEmployee(savedEmployee.getId())).toUri())
                .body(employeeAssembler.toModel(savedEmployee));
    }

    /**
     * function that handle Get request for get all exiting employees
     * @return Collection model of entities model of employees with status code "ok"
     */
    @GetMapping("/employees")
    public ResponseEntity<CollectionModel<EntityModel<Employee>>> getAllEmployees()
    {
        return ResponseEntity.ok(employeeAssembler.toCollectionModel(employeeRepository.findAll()));
    }

    /**
     * function which handle get request and receiving single employee by id
     * @param id  - represents id of the requested employee in DB
     * @return return Entity model of requested employee
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> getEmployee(@PathVariable String id)
    {
        return employeeRepository.findById(id)
                .map(employee -> employeeAssembler.toModel(employee)).map(ResponseEntity::ok)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    /**
     * function that handle Get request for get all exiting employees (as DTOs)
     * @return Collection model of entities model of Dtos employees with status code "ok"
     */
    @GetMapping("/employees/info")
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDto>>> getAllDtoEmployees()
    {
        return ResponseEntity.ok(
                employeeDtoAssembler.toCollectionModel(
                        StreamSupport.stream(employeeRepository.findAll().spliterator(),
                                        false)
                                .map(EmployeeDto::new)
                                .collect(Collectors.toList())));    }

    /**
     * function which handle get request get single DTO employee
     * @param id  - represents id of the requested employee in DB
     * @return return Entity model of requested employee as DTO
     */
    @GetMapping("/employees/{id}/info")
    public ResponseEntity<EntityModel<EmployeeDto>> getEmployeeDto(@PathVariable String id)
    {
        return employeeRepository.findById(id)
                .map(EmployeeDto::new)
                .map(employeeDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    /**
     * function which handle Get req and get employee not in the specific branch
     * @param brunchId id of requested branch
     * @return fitting employees as collection model of entity model of employee as dtos
     */
    @GetMapping("employees/filterByBranch")
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDto>>>
    getEmployeesNotInBranch(@RequestParam String brunchId){

        Branch brunch = brunchRepository.findById(brunchId)
                .orElseThrow(()-> new BranchNotFoundException(brunchId));

        List<Employee> fittingEmployees = employeeRepository.findAll()
                .stream().filter(employee -> !employee.getBranches().contains(brunch))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                employeeDtoAssembler.toCollectionModel(
                        StreamSupport.stream(fittingEmployees.spliterator(),
                                        false)
                                .map(EmployeeDto::new)
                                .collect(Collectors.toList())));

    }

    /**
     * function which handle Get req and get employee  in the specific branch
     * @param brunchId id of requested branch
     * @return fitting employees as collection model of entity model of employee as dtos
     */
    @GetMapping("employees/findByBranches/{brunchId}")
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDto>>>
    getByBranches(@PathVariable String brunchId){
        Branch brunch = brunchRepository.findById(brunchId)
                .orElseThrow(()-> new BranchNotFoundException(brunchId));

        List<Employee> fittingEmployees = employeeRepository.findByBranchesContains(brunch);
        return ResponseEntity.ok(
                employeeDtoAssembler.toCollectionModel(
                        StreamSupport.stream(fittingEmployees.spliterator(),
                                        false)
                                .map(EmployeeDto::new)
                                .collect(Collectors.toList())));

    }

    /**
     * function that handle put req for updating an existing employee
     * @param newEmployee employee with updated data
     * @param id of requested employee to updated
     * @return updated employee
     */
    @PutMapping("employees/{id}")
    public ResponseEntity<EntityModel<Employee>> updateEmployee(@RequestBody Employee newEmployee,
                                                                   @PathVariable String id)
    {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new EmployeeNotFoundException(id));
        newEmployee.setId(id);
        newEmployee.setIdNumber(employee.getIdNumber());
        newEmployee.setBranches(employee.getBranches());
        Employee updateEmployee = customBrunchRepository.updateEmployee(newEmployee);
//        Employee updatedEmployee = employeeRepository.findById(id).map(employee -> {
//            employee.setBranches(newEmployee.getBranches());
//            employee.setSalaryPerHour(newEmployee.getSalaryPerHour());
//            employee.setFirstName(newEmployee.getFirstName());
//            employee.setLastName(newEmployee.getLastName());
//            employee.setIdNumber(employee.getIdNumber());
//            employee.setBranches(employee.getBranches());
//            return employeeRepository.save(employee);
//        }).orElseThrow(() -> new EmployeeNotFoundException(id));

        EntityModel<Employee> employeeEntityModel =
                employeeAssembler.toModel(updateEmployee);
        return ResponseEntity.created(employeeEntityModel.getRequiredLink(
                IanaLinkRelations.SELF
        ).toUri()).body(employeeEntityModel);
    }

    /**
     * function which handle delete req and delete employee
     * @param id of requested employee to be deleted
     */
    @DeleteMapping("employees/{id}")
    public void deleteEmployee(@PathVariable String id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        System.out.println(employee.getBranches());
        for ( Branch branch : employee.getBranches() )
        {
            customBrunchRepository.deleteEmployee(branch, id);
        }
        employeeRepository.delete(employee);

    }

}
