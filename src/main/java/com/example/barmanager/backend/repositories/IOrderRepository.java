package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.queryresults.DrinkCount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IOrderRepository extends MongoRepository<Order, String> {
}
