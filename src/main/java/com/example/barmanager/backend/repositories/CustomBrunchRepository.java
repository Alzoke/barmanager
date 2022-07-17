package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.exceptions.EmployeeNotFoundException;
import com.example.barmanager.backend.models.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomBrunchRepository implements ICustomBrunchRepository
{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IEmployeeRepository employeeRepository;

    @Override
    public void addEmployee(Branch brunch, Employee employee)
    {
        // first direction (brunch -> employee)
        employee.getBranches().add(brunch);
        UpdateResult  updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(brunch.getId()))
                .apply(new Update().push("employeesIds", employee.getId())).first();
        System.out.println(updateResultBrunch);

        // second direction (employee -> brunch)
        UpdateResult  updateResultEmployee = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(new Update().push("branches", brunch)).first();
        System.out.println(updateResultEmployee);


    }



    @Override
    public void addOrder(Branch brunch, Order order)
    {
        UpdateResult  updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(brunch.getId()))
                .apply(new Update().push("orders", order)).first();
        System.out.println(updateResultBrunch);
    }

    @Override
    public void removeEmployee(Branch branch, Employee employee)
    {
//        brunch.getEmployeesIds().remove(employee.getId());
        List<String> employeesIds = branch.getEmployeesIds().stream()
                .filter(employeeId -> employeeId != employee.getId())
                .collect(Collectors.toList());
        List<Branch> branches = employee.getBranches().stream()
                .filter(currentBranch -> currentBranch.getId() != branch.getId())
                .collect(Collectors.toList());
        employee.getBranches().remove(branch);
        branch.getEmployeesIds().remove(employee.getId());
//        System.out.println(branches);
        // first direction (brunch -> employee)
        UpdateResult  updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().set("employeesIds", branch.getEmployeesIds())).first();
        System.out.println(updateResultBrunch);

        // second direction (employee -> brunch)
        UpdateResult  updateResultEmployee = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(new Update().set("branches", employee.getBranches())).first();
        System.out.println(updateResultEmployee);
    }

    @Override
    public Employee updateEmployee(Employee employee)
    {
        Update update = new Update();
        update.set("salaryPerHour",employee.getSalaryPerHour());
        update.set("idNumber",employee.getIdNumber());
        update.set("firstName",employee.getFirstName());
        update.set("lastName", employee.getLastName());
        update.set("branches",employee.getBranches());
        UpdateResult updateResult = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(update).first();

        return employee;
    }

    @Override
    public boolean deleteEmployee(Branch branch,String employeeIdToRemove)
    {
        List<String> employeesIds = branch.getEmployeesIds();
        List<String> updatedEmployees = employeesIds.stream().filter(id ->
                        !id.equals(employeeIdToRemove))
                .collect(Collectors.toList());
        System.out.println(updatedEmployees);

        UpdateResult first = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().set("employeesIds", updatedEmployees)).first();
        System.out.println(first);

        // return true if deletion succeeded
        return  first.getMatchedCount() > 0 && first.getModifiedCount() > 0;

    }
    @Override
    public boolean removeBranch(Branch branch)
    {
        List<String> employeesIds = branch.getEmployeesIds();
        for ( String employeesId : employeesIds )
        {
            Employee employee = employeeRepository.findById(employeesId)
                    .orElseThrow(() -> new EmployeeNotFoundException(employeesId));
            employee.getBranches().remove(branch);

            updateEmployee(employee);
        }

        DeleteResult remove = mongoTemplate.remove(branch);

        return remove.getDeletedCount() > 0;
    }
}
