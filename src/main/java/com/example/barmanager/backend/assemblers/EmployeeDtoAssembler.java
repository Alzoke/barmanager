package com.example.barmanager.backend.assemblers;

import com.example.barmanager.backend.controllers.EmployeesController;
import com.example.barmanager.backend.models.EmployeeDto;
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
public class EmployeeDtoAssembler implements RepresentationModelAssembler<EmployeeDto, EntityModel<EmployeeDto>>
{
    @Override
    public EntityModel<EmployeeDto> toModel(EmployeeDto employeeDto )
    {
        return EntityModel.of(employeeDto,
                WebMvcLinkBuilder.linkTo(methodOn(EmployeesController.class).getEmployeeDto(employeeDto.getEmployee()
                        .getId())).withSelfRel(),
                linkTo(methodOn(EmployeesController.class).getAllDtoEmployees()).withRel("all employees"));     }

    @Override
    public CollectionModel<EntityModel<EmployeeDto>> toCollectionModel(Iterable<? extends EmployeeDto> employeeDtos)
    {
        List<EmployeeDto> orderDtos = (List<EmployeeDto>) employeeDtos;
        List<EntityModel<EmployeeDto>> orders =
                orderDtos.stream().map(employeeDto -> this.toModel(employeeDto))
                        .collect(Collectors.toList());
        Link link = linkTo(methodOn(EmployeesController.class).getAllDtoEmployees()).withSelfRel();
        return CollectionModel.of(orders,link);
    }
}
