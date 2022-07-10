package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.models.eOrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface IOrderRepository extends MongoRepository<Order, String> {
    List<Order> findByOrderDateBetween(LocalDate startDate,LocalDate endDate);
    List<Order> findByOrderStatus(eOrderStatus orderStatus);
}
