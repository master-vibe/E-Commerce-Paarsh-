package com.paarsh.admin_paarsh.repository;

import com.paarsh.admin_paarsh.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByOrderDate(Date reportDate);
}
