package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Brunch;
import com.example.barmanager.backend.models.Employee;

public interface ICustomBrunchRepository
{
    void addEmployee(Brunch brunch, Employee employee);

}
