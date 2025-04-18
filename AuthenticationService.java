package com.example.onlinebookshop.service;

import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication tasks
 * Applies the Service Layer pattern to isolate business logic
 */
@Service
public class AuthenticationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public AuthenticationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Authenticate a user by email and password
     * @param email The user's email
     * @param password The user's password
     * @return Authenticated Customer object or null if authentication fails
     */
    public Customer authenticate(String email, String password) {
        // Try to find customer by email
        Customer customer = customerRepository.findByEmail(email);

        // Check if customer exists and password matches
        if (customer != null && password.equals(customer.getPassword())) {
            return customer;
        }

        // Authentication failed
        return null;
    }

    /**
     * Check if user has admin role
     * @param customer The customer to check
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin(Customer customer) {
        return customer != null && "ADMIN".equals(customer.getRole());
    }
}