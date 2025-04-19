package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Book;
import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.model.ShoppingCart;
import com.example.onlinebookshop.repository.BookRepository;
import com.example.onlinebookshop.repository.CustomerRepository;
import com.example.onlinebookshop.repository.ShoppingCartRepository;
import com.example.onlinebookshop.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // ✅ ADDED
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookRepository bookRepository;

    // Show the checkout payment options page
    @GetMapping
    public String showCheckoutPage(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/home";
        }

        List<ShoppingCart> cartItems = shoppingCartRepository.findByCustomer(customer);

        double total = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("customer", customer);
        model.addAttribute("total", total);
        return "checkout";
    }

    // Handle checkout and reduce stock
    @Transactional // not sure if this works but stick over say i need this to update DB
    @PostMapping
    public String processCheckout(@RequestParam String method, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            return "redirect:/home";
        }

        List<ShoppingCart> cartItems = shoppingCartRepository.findByCustomer(customer);

        double total = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Choose the payment strategy
        PaymentStrategy strategy;
        switch (method) {
            case "paypal" -> strategy = new PayPalPayment();
            case "cod" -> strategy = new CashOnDelivery();
            default -> strategy = new VisaPayment();
        }

        // Reduce book quantity
        for (ShoppingCart item : cartItems) {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                int updatedQuantity = book.getQuantity() - item.getQuantity();
                book.setQuantity(Math.max(0, updatedQuantity));
                bookRepository.save(book);
            }
        }

        // Apply payment
        String paymentMessage = strategy.pay(total);

        // Clear cart after payment
        shoppingCartRepository.deleteByCustomer(customer);

        // Send to confirmation page
        model.addAttribute("message", paymentMessage);
        model.addAttribute("total", total);

        return "checkoutConfirmation";
    }
}
