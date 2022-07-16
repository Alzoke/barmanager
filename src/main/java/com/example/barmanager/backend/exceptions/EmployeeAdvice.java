package com.example.barmanager.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EmployeeAdvice
{
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmployeeNotFoundException.class)
    String employeeNotFoundHandler(EmployeeNotFoundException notFoundException)
    {
        return notFoundException.getMessage();
    }

    /**
     * handler for saving new employee error
     * @param exception - null pointer exception
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    String nullPointerHandler(RuntimeException exception)
    {
        return exception.getMessage();
    }

}
