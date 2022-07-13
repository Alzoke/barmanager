package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.EmployeesController;
import com.example.barmanager.backend.models.Employee;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Component
public class EmployeeAssembler  implements RepresentationModelAssembler<Employee, EntityModel<Employee>>
{

    @Override
    public EntityModel<Employee> toModel(Employee employee)
    {
        return EntityModel.of(employee,
                WebMvcLinkBuilder.linkTo(methodOn(EmployeesController.class).getEmployee(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeesController.class).getAllEmployees()).withRel("All employees"));
    }

    @Override
    public CollectionModel<EntityModel<Employee>> toCollectionModel(Iterable<? extends Employee> entities)
    {
        List<Employee> employees = (List<Employee>) entities;

        List<EntityModel<Employee>> entityEmployees = employees.stream().map(this::toModel)
                .collect(Collectors.toList());

        Link link = linkTo(methodOn(EmployeesController.class).getAllEmployees()).withSelfRel();
        return CollectionModel.of(entityEmployees, link);    }
}
