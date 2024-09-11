package com.paarsh.admin_paarsh.repository;

import com.paarsh.admin_paarsh.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can define custom queries here if needed
}