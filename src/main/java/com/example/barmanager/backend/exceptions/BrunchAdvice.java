package com.example.barmanager.backend.exceptions;

import com.example.barmanager.backend.models.Brunch;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BrunchAdvice
{
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BrunchNotFoundException.class)
    String brunchNotFoundHandler(BrunchNotFoundException notFoundException)
    {
        return notFoundException.getMessage();
    }

}
