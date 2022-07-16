package com.example.barmanager.backend.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Document("Branches")
@NoArgsConstructor
public class Branch
{
    @Id private String id;
    private String branchName;
    private String city;
    private String country;

    @DocumentReference
    private List<Order> orders;

    private List<String> employeesIds;

    public Branch(String brunchName,String country,String city)
    {
        this.branchName = brunchName;
        this.country = country;
        this.city = city;
        this.employeesIds = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o ) return true;
        if ( !(o instanceof Branch) ) return false;
        Branch branch = (Branch) o;
        return Objects.equals(getId(), branch.getId()) && Objects.equals(getBranchName(), branch.getBranchName()) && Objects.equals(getOrders(), branch.getOrders()) && Objects.equals(getEmployeesIds(), branch.getEmployeesIds());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getId(), getBranchName(), getOrders(), getEmployeesIds());
    }
}
