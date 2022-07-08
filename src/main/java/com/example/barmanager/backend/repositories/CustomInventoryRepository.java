package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class CustomInventoryRepository implements ICustomInventoryRepository{
    @Autowired
    public MongoTemplate mongoTemplate;

    @Override
    public List<Document> getCountGroupByCategory() {
        Aggregation aggregation = newAggregation(
                group("category").count().as("count"),
                project("count").and("category"));

        System.out.println(aggregation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, BarDrink.class, Document.class);

        return results.getMappedResults();
    }

    @Override
    public Iterable<? extends BarDrink> getFilteredByMultipleParams(Optional<String> category, Optional<String> ingredient,
                                                                    Optional<String> alcoholFilter, Optional<Double> minPrice,
                                                                    Optional<Double> maxPrice) {
        Query query = new Query();
        category.ifPresent(s -> query.addCriteria(Criteria.where("category").is(s)));
        alcoholFilter.ifPresent(s -> query.addCriteria(Criteria.where("isAlcoholic").is(s)));
        ingredient.ifPresent(s -> query.addCriteria(Criteria.where("ingredients").in(s)));
        query.addCriteria(Criteria.where("price").lte(maxPrice.orElse(Double.MAX_VALUE)).gte(minPrice.orElse(0.0)));

        return  mongoTemplate.find(query,BarDrink.class,"barinventory");
    }


}
