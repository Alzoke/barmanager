package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.models.Brunch;
import com.example.barmanager.backend.models.Employee;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class CustomBrunchRepository implements ICustomBrunchRepository
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void addEmployee(Brunch brunch, Employee employee)
    {
        // first direction (brunch -> employee)
        UpdateResult  updateResultBrunch = mongoTemplate.update(Brunch.class)
                .matching(Criteria.where("_id").is(brunch.getId()))
                .apply(new Update().push("employeesIds", employee.getId())).first();
        System.out.println(updateResultBrunch);

        // second direction (employee -> brunch)
        UpdateResult  updateResultEmployee = mongoTemplate.update(Employee.class)
                .matching(Criteria.where("_id").is(employee.getId()))
                .apply(new Update().push("brunches", brunch)).first();
        System.out.println(updateResultEmployee);


    }
}
