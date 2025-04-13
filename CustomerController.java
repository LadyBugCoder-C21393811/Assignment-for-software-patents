package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.repository.CustomerRepository;
import com.example.onlinebookshop.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller handling customer authentication and registration
 * Uses Repository pattern for data access
 */
@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final AuthenticationService authService;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, AuthenticationService authService) {
        this.customerRepository = customerRepository;
        this.authService = authService;
    }

    /**
     * Display login/signup page or redirect if already logged in
     */
    @GetMapping("/home")
    public String showLoginAndSignupPage(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("customer");

        if (loggedInCustomer != null) {
            // Redirect based on role
            return "ADMIN".equals(loggedInCustomer.getRole())
                    ? "redirect:/books/add"
                    : "redirect:/books/view";
        }

        // If not logged in, show the login/signup page
        model.addAttribute("customer", new Customer());
        return "home";
    }

    /**
     * Redirect root URL to home page
     */
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    /**
     * Process user logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");
        return "redirect:/home";
    }

    /**
     * Process user registration
     */
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        // Check if email already exists
        if (customerRepository.findByEmail(customer.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("signupError", "Email already registered. Please use a different email.");
            return "redirect:/home";
        }

        // Set default role and save
        customer.setRole("USER");
        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("signupSuccess", "Registration successful! Please log in.");
        return "redirect:/home";
    }

    /**
     * Process user login
     */
    @PostMapping("/login")
    public String processLogin(@ModelAttribute Customer customer, Model model, HttpSession session,
                               RedirectAttributes redirectAttributes) {

        String email = customer.getEmail().trim();
        String password = customer.getPassword().trim();

        // Attempt authentication
        Customer authenticatedCustomer = authService.authenticate(email, password);

        if (authenticatedCustomer != null) {
            // Authentication successful
            session.setAttribute("customer", authenticatedCustomer);

            // Redirect based on role
            return "ADMIN".equals(authenticatedCustomer.getRole())
                    ? "redirect:/books/add"
                    : "redirect:/books/view";
        }

        // Authentication failed
        redirectAttributes.addFlashAttribute("loginError", "Invalid email or password");
        return "redirect:/home";
    }
}