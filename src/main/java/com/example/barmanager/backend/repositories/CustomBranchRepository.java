package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.exceptions.EmployeeNotFoundException;
import com.example.barmanager.backend.models.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * class that implements  ICustomBranchRepository
 * contains custom queries and functions that works with the DB
 */
@Component
public class CustomBranchRepository implements ICustomBranchRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IEmployeeRepository employeeRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomBranchRepository.class);

    /**
     * custom function which inserts employee to a branch and make branch contain this employee
     * many-to-many relationship
     *
     * @param branch   where employee should be inserted
     * @param employee to be inserted
     */
    @Override
    public void addEmployee(Branch branch, Employee employee) {
        // first direction (branch -> employee)
        employee.getBranches().add(branch);
        UpdateResult updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().push("employeesIds", employee.getId())).first();
        logger.info(updateResultBrunch.toString());

        // second direction (employee -> branch)
        UpdateResult updateResultEmployee = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(new Update().push("branches", branch)).first();
        logger.info(updateResultEmployee.toString());

    }

    /**
     * function that insert order to specific branch
     * may need to be deleted
     *
     * @param branch
     * @param order
     */
    @Override
    public void addOrder(Branch branch, Order order) {
        UpdateResult updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().push("orders", order)).first();
        logger.info(updateResultBrunch.toString());
    }

    /**
     * function that remove employee from DB from specific branch
     *
     * @param branch   to remove from
     * @param employee to be removed
     */
    @Override
    public void removeEmployee(Branch branch, Employee employee) {
//        List<String> employeesIds = branch.getEmployeesIds().stream()
//                .filter(employeeId -> employeeId != employee.getId())
//                .collect(Collectors.toList());
//        List<Branch> branches = employee.getBranches().stream()
//                .filter(currentBranch -> currentBranch.getId() != branch.getId())
//                .collect(Collectors.toList());
        employee.getBranches().remove(branch);
        branch.getEmployeesIds().remove(employee.getId());
        // first direction (brunch -> employee)
        UpdateResult updateResultBrunch = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().set("employeesIds", branch.getEmployeesIds())).first();
        logger.info(updateResultBrunch.toString());

        // second direction (employee -> brunch)
        UpdateResult updateResultEmployee = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(new Update().set("branches", employee.getBranches())).first();
        logger.info(String.valueOf(updateResultEmployee));


    }

    /**
     * function that responsible to
     * updating employee data in the DB
     *
     * @param employee to be updated
     * @return the updated employee
     */
    @Override
    public Employee updateEmployee(Employee employee) {
        Update update = new Update();
        update.set("salaryPerHour", employee.getSalaryPerHour());
        update.set("idNumber", employee.getIdNumber());
        update.set("firstName", employee.getFirstName());
        update.set("lastName", employee.getLastName());
        update.set("branches", employee.getBranches());
        UpdateResult updateResult = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(update).first();
        logger.info("update results" + updateResult);

        return employee;
    }

    /**
     * function which responsible to pop out employee
     * from given branch
     *
     * @param branch             requested branch
     * @param employeeIdToRemove id of the requested employee
     * @return boolean indicate whether the action succeeded or not
     */
    @Override
    public boolean deleteEmployee(Branch branch, String employeeIdToRemove) {
        // first direction -> removing employee from current employees in the given branch
        List<String> employeesIds = branch.getEmployeesIds();
        List<String> updatedEmployees = employeesIds.stream().filter(id ->
                        !id.equals(employeeIdToRemove))
                .collect(Collectors.toList());
        logger.info("Employees after update:" + updatedEmployees);

        UpdateResult first = mongoTemplate.update(Branch.class)
                .matching(Criteria.where("_id").is(branch.getId()))
                .apply(new Update().set("employeesIds", updatedEmployees)).first();
        logger.info("result: " + first);

        // return true if deletion succeeded
        return first.getMatchedCount() > 0 && first.getModifiedCount() > 0;

    }

    /**
     * function which responsible for removing branch from DB
     *
     * @param branch to be removed
     * @return boolean that indicates if deleted or not
     */
    @Override
    public boolean removeBranch(Branch branch) {
        List<String> employeesIds = branch.getEmployeesIds();
        // removing branch from branch list of each employee which belongs to this branch
        for (String employeesId : employeesIds) {
            Employee employee = employeeRepository.findById(employeesId)
                    .orElseThrow(() -> new EmployeeNotFoundException(employeesId));
            employee.getBranches().remove(branch);

            updateEmployee(employee);
        }

        DeleteResult remove = mongoTemplate.remove(branch);

        return remove.getDeletedCount() > 0;
    }
}
