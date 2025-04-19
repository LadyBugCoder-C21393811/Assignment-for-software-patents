package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Order;
import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.repository.OrderRepository;
import com.example.onlinebookshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/create")
    public Order createOrder(@RequestParam Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Order order = new Order(LocalDateTime.now(), customerOpt.get());
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Customer not found");
        }
    }
}
