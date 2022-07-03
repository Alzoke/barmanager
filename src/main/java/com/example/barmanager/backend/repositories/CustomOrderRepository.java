package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Order;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

/*
public class CustomOrderRepository implements ICustomOrderRepository
{
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Order saveNewOrder(Order order)
    {
        Order savedOrder = mongoTemplate.save(order);
        UpdateResult result = mongoTemplate.update(Order.class)
                .matching(Criteria.where("drinksId").is(order.getDrinkIds()));
//                .apply(new Update().push())
    }
}
*/
