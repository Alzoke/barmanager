package com.example.barmanager.backend.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@Document("Brunchs")
@NoArgsConstructor
public class Brunch
{
    @Id private String id;
    private String brunchName;
    @DocumentReference
    private List<BarDrink> barDrinks;
    @DocumentReference
    private List<Order> orders;
    private List<String> employeesIds;

    public Brunch(String brunchName)
    {
        this.brunchName = brunchName;
        this.employeesIds = new ArrayList<>();
    }
}
