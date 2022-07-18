package com.example.barmanager.backend.service;

import com.example.barmanager.backend.models.Customer;
import com.example.barmanager.backend.models.Order;
import com.example.barmanager.backend.models.eOrderStatus;
import com.example.barmanager.backend.repositories.CustomOrderRepository;
import com.example.barmanager.backend.repositories.IOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService
{
    private final CustomOrderRepository customOrderRepository;
    private final IOrderRepository orderRepository;

    public OrderService(CustomOrderRepository orderRepository, IOrderRepository iorderRepository)
    {
        this.customOrderRepository = orderRepository;
        this.orderRepository = iorderRepository;
    }

    public void saveNewOrder(Order order, Customer customer)
    {
        //customOrderRepository.saveNewOrder(order);
    }

    public List<Order> getOpenOrder()
    {
        List<Order> orders = orderRepository.findByOrderStatus(eOrderStatus.Open);

        return orders;
    }

    public List<Order> getOrderBetweenDates(String sDate,String eDate){
        LocalDate startDate = LocalDate.parse(sDate);
        LocalDate endDate = LocalDate.parse(eDate);
        List<Order> fittingOrders = orderRepository.findByOrderDateBetween(startDate, endDate);

        return fittingOrders;
    }
}
