package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import java.util.List;
import org.bson.Document;

@Component
public class CustomOrderRepository implements ICustomOrderRepository
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Order saveNewOrder(Order order)
    {

        Order savedOrder = mongoTemplate.save(order);
        UpdateResult customerUpdateResult = mongoTemplate.update(Customer.class)
                .matching(Criteria.where("_id")
                        .is(order.getCustomer().getCustomerId()))
                .apply(new Update().push("ordersIds", order.getOrderId())).first();

        UpdateResult OrderUpdateResult = mongoTemplate.update(Order.class)
                .matching(Criteria.where("_id").is(order.getOrderId()))
                .apply(new Update().set("customer",order.getCustomer())).first();

       /* UpdateResult result = mongoTemplate.update(Order.class)
                .matching(Criteria.where("id").is(order.getOrderId()))
                .apply(new Update().addToSet("customer",order.getOrderId())).first();
        System.out.println(result)*/;
        System.out.println(customerUpdateResult);
        System.out.println(OrderUpdateResult);

        return savedOrder;
    }

    @Override
    public List<Document> getMostOrderedDrinks() {
        UnwindOperation unwindOperation = unwind("orderedDrinks");
        GroupOperation groupOperation = group("orderedDrinks").count().as("count");
        ProjectionOperation projectionOperation = project().andExpression("orderedDrinks").as("drink id")
                .andExpression("count").as("count");
        Aggregation aggregation = newAggregation(unwindOperation, groupOperation, projectionOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, Order.class, Document.class);

        return results.getMappedResults();
    }


}
