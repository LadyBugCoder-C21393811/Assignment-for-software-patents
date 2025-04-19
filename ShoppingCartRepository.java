package com.example.onlinebookshop.repository;

import com.example.onlinebookshop.model.ShoppingCart;
import com.example.onlinebookshop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    // Get all items for a specific customer
    List<ShoppingCart> findByCustomer(Customer customer);

    //  Get a specific book in the cart (used for updating quantity)
    ShoppingCart findByCustomerAndBookId(Customer customer, Long bookId);

    //  Delete/ remove  all cart items for a specific customer (e.g. after checkout)
    void deleteByCustomer(Customer customer);
}
