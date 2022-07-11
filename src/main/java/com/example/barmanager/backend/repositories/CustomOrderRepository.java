package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

@Component
public class CustomOrderRepository implements ICustomOrderRepository
{
     private final Logger logger = LoggerFactory.getLogger(CustomOrderRepository.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ICustomerRepository customerRepository;

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
    public void deleteOrder(Order order)
    {

        logger.info("removing: " + mongoTemplate.remove(order));
        Customer customer = customerRepository.findById(order.getCustomer().getCustomerId()).get();
        ArrayList<String> ordersIds = customer.getOrdersIds();
        List<String> orders = ordersIds.stream()
                .filter(orderId -> !orderId.equals(order.getOrderId()))
                .collect(Collectors.toList());

        UpdateResult first = mongoTemplate.update(Customer.class)
                .matching(Criteria.where("_id").is(order.getCustomer().getCustomerId()))
                .apply(new Update().set("ordersIds", orders)).first();
        logger.info(first.toString());





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
