package com.example.barmanager.backend.repositories;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.models.eOrderStatus;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends MongoRepository<Order, String> {
    List<Order> findByOrderDateBetween(LocalDate startDate,LocalDate endDate);
    List<Order> findByOrderStatus(eOrderStatus orderStatus);
//    List<Order> findByOrderStatusAndBranch(eOrderStatus orderStatus, Branch branch);
    Optional<Order> findByOrderStatusAndSeatNumber(eOrderStatus orderStatus, int seatNum);
}
