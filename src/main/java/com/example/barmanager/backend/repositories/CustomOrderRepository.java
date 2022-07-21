package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.exceptions.CustomerNotFoundException;
import com.example.barmanager.backend.exceptions.OrderNotFoundException;
import com.example.barmanager.backend.models.*;
import com.example.barmanager.backend.service.CustomerService;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;

/**
 * class that implements  ICustomOrderRepository
 * contains custom queries and functions that works with the DB
 */
@Component
public class CustomOrderRepository implements ICustomOrderRepository {
    private final Logger logger = LoggerFactory.getLogger(CustomOrderRepository.class);
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ICustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;

    /**
     * saves new order into DB
     *
     * @param order to be saved
     * @return saved order
     */
    @Override
    public Order saveNewOrder(Order order) {

        Order savedOrder = mongoTemplate.save(order);
        UpdateResult customerUpdateResult = mongoTemplate.update(Customer.class)
                .matching(Criteria.where("_id")
                        .is(order.getCustomer().getCustomerId()))
                .apply(new Update().push("ordersIds", order.getOrderId())).first();

        UpdateResult OrderUpdateResult = mongoTemplate.update(Order.class)
                .matching(Criteria.where("_id").is(order.getOrderId()))
                .apply(new Update().set("customer", order.getCustomer())).first();

        logger.info(customerUpdateResult.toString());
        logger.info(OrderUpdateResult.toString());

        return savedOrder;
    }

    /**
     * delete order form DB
     *
     * @param order to be deleted
     * @return boolean indicates if order was deleted
     */
    @Override
    public boolean deleteOrder(Order order) {
        Customer customer = customerRepository.findById(order.getCustomer().getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(order.getCustomer().getCustomerId()));

        ArrayList<String> ordersIds = customer.getOrdersIds();
        List<String> orders = ordersIds.stream()
                .filter(orderId -> !orderId.equals(order.getOrderId()))
                .collect(Collectors.toList());

        UpdateResult first = mongoTemplate.update(Customer.class)
                .matching(Criteria.where("_id").is(order.getCustomer().getCustomerId()))
                .apply(new Update().set("ordersIds", orders)).first();
        logger.info(first.toString());


        logger.info("removing: " + mongoTemplate.remove(order));

        // return true if deletion succeeded
        return first.getMatchedCount() > 0 && first.getModifiedCount() > 0;

    }

    /**
     * update order and change her status to "Close"
     *
     * @param order to be closes
     * @return updated order
     */
    @Override
    public Order closeOrder(Order order) {
        Order order1 = orderRepository.findById(order.getOrderId()).get();
        Customer customer = customerService.findCustomerByIdNumber(order1.getCustomer().getIdNumber());
        Update update = new Update();
        update.set("orderedDrinks", order1.getOrderedDrinks());
        update.set("bill", order1.getBill());
        update.set("orderDate", order1.getOrderDate());
        update.set("orderStatus", eOrderStatus.Close);
        update.set("seatNumber", order1.getSeatNumber());
        update.set("customer", customer);
        UpdateResult updateResult = mongoTemplate.update(Order.class)
                .matching(Criteria.where("_id").is(order.getOrderId()))
                .apply(update).first();

        logger.info(updateResult.toString());

        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElseThrow(() ->
                new OrderNotFoundException(order.getOrderId()));

        return updatedOrder;
    }



    /**
     * checks whether seat has an open order (the seats is taken)
     *
     * @param seatNumber to be checked
     * @return optional order
     */
    @Override
    public Optional<Order> findCloseBySeat(int seatNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("seatNumber").is(seatNumber));
        query.addCriteria(Criteria.where("orderStatus").is(eOrderStatus.Open));
        List<Order> orders = mongoTemplate.find(query, Order.class);

        if (orders.isEmpty()) {
            return Optional.of(new Order());
        }
        /* orders.get(0) -> Because at a time there can be at most one
         open order in  the seat
        */
        return Optional.of(orders.get(0));
    }

/*
    public Optional<List<Order>> findOpenByBranch(Branch branch)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("branch").is(branch));
        query.addCriteria(Criteria.where("orderStatus").is(eOrderStatus.Open));
        List<Order> orders = mongoTemplate.find(query, Order.class);
//        logger.info(String.valueOf(orders.get(0).getOrderedDrinks().size()));
        if ( orders.isEmpty() )
        {
            return Optional.of(orders = new ArrayList<>());
        }

        return Optional.of(orders);
    }
*/

    /**
     * find and return the most (10) ordered drinks
     *
     * @return the most (10) ordered drinks
     */
    @Override
    public List<Document> getTenMostOrderedDrinks() {
        UnwindOperation unwindOperation = unwind("orderedDrinks");
        GroupOperation groupOperation = group("orderedDrinks").count().as("result");
        SortOperation sortOperation = sort(Sort.by(Sort.Direction.DESC, "result"));
        ProjectionOperation projectionOperation = project().andExpression("orderedDrinks")
                .as("drink id")
                .andExpression("result").as("result");
        LimitOperation limitOperation = limit(10);
        Aggregation aggregation = newAggregation(unwindOperation, groupOperation, sortOperation,
                projectionOperation, limitOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, Order.class, Document.class);
        for (Document document : results.getMappedResults()) {
            document.put("_id", document.get("_id").toString());
        }
        return results.getMappedResults();
    }

    @Override
    public List<Document> getProfitsByYear(int year) {
        ProjectionOperation projectDateAsMonthAndYear =
                project().and(DateOperators.Month.monthOf("orderDate")).as("month")
                        .and(DateOperators.Year.yearOf("orderDate")).as("year")
                        .and("bill").as("bill");
        MatchOperation matchOperation = match(Criteria.where("year").is(year));
        GroupOperation groupOperation = group("month").sum("bill").as("result");
        SortOperation sortOperation = sort(Sort.by(Sort.Direction.ASC, "_id"));
        ProjectionOperation projectionOperation = project().andExpression("month").as("month")
                .andExpression("result").as("result");
        Aggregation aggregation = newAggregation(projectDateAsMonthAndYear, matchOperation, groupOperation,
                sortOperation, projectionOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, Order.class, Document.class);

        return results.getMappedResults();
    }


}
