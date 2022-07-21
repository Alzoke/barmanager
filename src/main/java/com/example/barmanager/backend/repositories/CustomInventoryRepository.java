package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.BarDrink;
import com.example.barmanager.backend.models.Order;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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

    /**
     * Count and GroupBy categories
     * @return List of Documents , when each document contains the category name and its count.
     */
    @Override
    public List<Document> getCountGroupByCategory() {
        Aggregation aggregation = newAggregation(
                group("category").count().as("result"),
                project("result").and("category"));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, BarDrink.class, Document.class);

        return results.getMappedResults();
    }

    /**
     * Retrieve filtered drinks collection from the inventory by a set of optional predefined parameters as filter parameters.
     * @param category Optional , filter by category.
     * @param ingredient Optional , filter by ingredient.
     * @param alcoholFilter Optional , filter by alcoholic type.
     * @param minPrice Optional (default value is 0.0), filter by minimum price.
     * @param maxPrice Optional (default value id Double.MAX_VALUE), filter by maximum price
     * @return Filtered Collection of BarDrinks
     */
    @Override
    public Iterable<? extends BarDrink> getFilteredByMultipleParams(Optional<String> category,
                                                                    Optional<String> ingredient,
                                                                    Optional<String> alcoholFilter,
                                                                    Optional<Double> minPrice,
                                                                    Optional<Double> maxPrice) {
        Query query = new Query();
        category.ifPresent(s -> query.addCriteria(Criteria.where("category").is(s)));
        alcoholFilter.ifPresent(s -> query.addCriteria(Criteria.where("isAlcoholic").is(s)));
        ingredient.ifPresent(s -> query.addCriteria(Criteria.where("ingredients").in(s)));
        query.addCriteria(Criteria.where("price").lte(maxPrice.orElse(Double.MAX_VALUE)).gte(minPrice.orElse(0.0)));

        return  mongoTemplate.find(query,BarDrink.class,"barinventory");
    }

    /**
     * Count and GroupBy ingredients
     * @return List of Documents , when each document contains the ingredient name and its count.
     */
    @Override
    public List<Document> getIngredientCount() {
        //The ingredients for each "BarDrink"(Document) is contained in an array of ingredients
        UnwindOperation unwindOperation = unwind("ingredients");
        GroupOperation groupOperation = group("ingredients").count().as("result");
        ProjectionOperation projectionOperation = project().andExpression("ingredients")
                .as("ingredient name")
                .andExpression("result").as("result");
        AggregationResults<Document> results = mongoTemplate.aggregate(
                newAggregation(unwindOperation, groupOperation, projectionOperation), BarDrink.class, Document.class);
        return results.getMappedResults();
    }

}
