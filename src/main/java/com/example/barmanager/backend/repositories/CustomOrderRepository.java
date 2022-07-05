package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.queryresults.DrinkCount;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import java.util.List;

@Component
public class CustomOrderRepository implements ICustomOrderRepository
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveNewOrder(Order order,Customer customer)
    {
        Order savedOrder = mongoTemplate.save(order);
        Customer savedCustomer = mongoTemplate.save(customer);
        System.out.println(savedCustomer);
        System.out.println(savedOrder);
        UpdateResult customerUpdateResult = mongoTemplate.update(Customer.class)
                .matching(Criteria.where("_id")
                        .is(customer.getCustomerId()))
                .apply(new Update().push("orders", order.getOrderId())).first();

        UpdateResult OrderUpdateResult = mongoTemplate.update(Order.class)
                .matching(Criteria.where("_id").is(order.getOrderId()))
                .apply(new Update().set("customer",customer)).first();

       /* UpdateResult result = mongoTemplate.update(Order.class)
                .matching(Criteria.where("id").is(order.getOrderId()))
                .apply(new Update().addToSet("customer",order.getOrderId())).first();
        System.out.println(result)*/;
        System.out.println(customerUpdateResult);
        System.out.println(OrderUpdateResult);
    }

    @Override
    public List<DrinkCount> getMostOrderedDrinks() {
        UnwindOperation unwindOperation = unwind("orderedDrinks");
        GroupOperation groupOperation = group("orderedDrinks").count().as("count");
        ProjectionOperation projectionOperation = project().andExpression("_id").as("orderedDrinks")         // _id refers to the unwound category (i.e, cat1)
                .andExpression("count").as("count");

        Aggregation aggregation = newAggregation(unwindOperation, groupOperation, projectionOperation);

        AggregationResults<DrinkCount> results = mongoTemplate.aggregate(aggregation, BarDrink.class, DrinkCount.class);

        return results.getMappedResults();
    }


}
