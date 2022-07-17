package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Employee;
import com.example.barmanager.backend.models.Order;

public interface ICustomBrunchRepository
{
    void addEmployee(Branch brunch, Employee employee);
    void addOrder(Branch brunch, Order order);
    void removeEmployee(Branch branch, Employee employee);
    Employee updateEmployee(Employee employee);
    boolean deleteEmployee(Branch branch,String employeeIdToRemove);
    boolean removeBranch(Branch branch);


}
