package com.example.barmanager.backend.queryresults;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.annotation.Id;

public class DrinkCount {
    @Id
    public String id;
    public Integer count;
}
