package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Book;
import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.model.ShoppingCart;
import com.example.onlinebookshop.repository.BookRepository;
import com.example.onlinebookshop.repository.CustomerRepository;
import com.example.onlinebookshop.repository.ShoppingCartRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // View Cart with subtotal and optional error
    @GetMapping("/view")
    public String viewCart(Model model, HttpSession session, @RequestParam(required = false) String error) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/home";
        }

        List<ShoppingCart> cartItems = shoppingCartRepository.findByCustomer(customer);
        model.addAttribute("cartItems", cartItems);

        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("subtotal", subtotal);
        if ("stock".equals(error)) {
            model.addAttribute("errorMessage", "⚠️ Not enough stock available for one or more items.");
        }

        return "viewCart";
    }

    // Add to Cart
    @PostMapping("/add/{bookId}")
    public String addToCart(@PathVariable Long bookId, @RequestParam int quantity, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/home";
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.getQuantity() < quantity) {
            return "redirect:/books/view?error=invalid";
        }

        ShoppingCart existingItem = shoppingCartRepository.findByCustomerAndBookId(customer, bookId);
        if (existingItem != null) {
            int totalQuantity = existingItem.getQuantity() + quantity;
            if (totalQuantity > book.getQuantity()) {
                return "redirect:/cart/view?error=stock";
            }
            existingItem.setQuantity(totalQuantity);
        } else {
            ShoppingCart cartItem = new ShoppingCart();
            cartItem.setCustomer(customer);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
            cartItem.setImageUrl(book.getImageUrl());
            existingItem = cartItem;
        }

        shoppingCartRepository.save(existingItem);
        return "redirect:/cart/view";
    }

    // Update Cart Quantity
    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(@PathVariable Long cartItemId, @RequestParam int quantity) {
        ShoppingCart item = shoppingCartRepository.findById(cartItemId).orElse(null);

        if (item != null) {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null && quantity <= book.getQuantity()) {
                item.setQuantity(quantity);
                shoppingCartRepository.save(item);
            } else {
                return "redirect:/cart/view?error=stock";
            }
        }

        return "redirect:/cart/view";
    }

    // Delete Cart Item
    @PostMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable Long id) {
        shoppingCartRepository.deleteById(id);
        return "redirect:/cart/view";
    }
}
