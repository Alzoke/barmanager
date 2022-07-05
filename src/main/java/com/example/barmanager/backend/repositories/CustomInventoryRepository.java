package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.queryresults.CountByCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

import java.util.List;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
@Component
public class CustomInventoryRepository implements ICustomInventoryRepository{
    @Autowired
    public MongoTemplate mongoTemplate;

    @Override
    public List<CountByCategory> getCountGroupByCategory() {
        Aggregation aggregation = newAggregation(
                group("category").count().as("count"),
                project("count").and("category"));

        AggregationResults<CountByCategory> results = mongoTemplate.aggregate(aggregation, BarDrink.class, CountByCategory.class);

        return results.getMappedResults();
    }

}
