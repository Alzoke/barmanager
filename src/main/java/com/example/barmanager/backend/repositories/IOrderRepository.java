package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IOrderRepository extends MongoRepository<Order, String>
{
}
