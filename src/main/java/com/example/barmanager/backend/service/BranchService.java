package com.example.barmanager.backend.service;

import com.example.barmanager.backend.exceptions.BranchNotFoundException;
import com.example.barmanager.backend.exceptions.EmployeeNotFoundException;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Employee;
import com.example.barmanager.backend.repositories.CustomBrunchRepository;
import com.example.barmanager.backend.repositories.IBrunchRepository;
import com.example.barmanager.backend.repositories.IEmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class BranchService
{
    private final IBrunchRepository brunchRepository;
    private final CustomBrunchRepository customBrunchRepository;
    private final IEmployeeRepository employeeRepository;

    public BranchService(IBrunchRepository brunchRepository,
                         CustomBrunchRepository customBrunchRepository,
                         IEmployeeRepository employeeRepository)
    {
        this.brunchRepository = brunchRepository;
        this.customBrunchRepository = customBrunchRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * function that add new employee into Branch
     * @param employeeId id of employee to be added
     * @param branchId id of branch' employee need to be added in
     */
    public void addExistingEmployeeToBranch(String employeeId, String branchId)
    {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Branch branch = brunchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));

        customBrunchRepository.addEmployee(branch,employee);

    }

    /**
     * function that remove employee from branch
     * @param employeeId id of employee to be removed
     * @param branchId id of branch to remove from
     */
    public void removeExistingEmployeeToBranch(String employeeId, String branchId)
    {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Branch branch = brunchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));

        customBrunchRepository.removeEmployee(branch,employee);

    }

}
