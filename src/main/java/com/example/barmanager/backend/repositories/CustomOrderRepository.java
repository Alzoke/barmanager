package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

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
}
