package com.example.onlinebookshop.repository;

import com.example.onlinebookshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
