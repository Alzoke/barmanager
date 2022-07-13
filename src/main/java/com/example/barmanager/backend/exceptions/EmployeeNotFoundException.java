package com.example.barmanager.backend.exceptions;

public class EmployeeNotFoundException extends RuntimeException
{
    public EmployeeNotFoundException(String employeeId)
    {
        super(String.format("Cant find employee with corresponding id: %s", employeeId));
    }
}
