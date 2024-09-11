package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.model.Order;
import com.paarsh.admin_paarsh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        order.setOrderDate(new Date());
        order.setStatus("Pending");
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setTotalAmount(updatedOrder.getTotalAmount());
                    order.setStatus(updatedOrder.getStatus());
                    return orderRepository.save(order);
                }).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}