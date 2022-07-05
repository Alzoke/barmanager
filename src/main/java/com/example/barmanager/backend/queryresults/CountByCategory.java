package com.example.barmanager.backend.queryresults;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class CountByCategory {
    @Id
    public String category;
    public Integer count;
}
