package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Book;
import com.example.onlinebookshop.model.Customer;
import com.example.onlinebookshop.repository.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/books")
public class AdminBookController {

    @Autowired
    private BookRepository bookRepository;

    // Show the add book form
    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home"; // redirect non-admins to home
        }

        model.addAttribute("book", new Book());
        return "addBook";
    }

    // Handle form submission for adding a book
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home";
        }

        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/books/manage";
    }

    // Show all books for admin management
    @GetMapping("/manage")
    public String manageBooks(Model model, HttpSession session) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home";
        }

        model.addAttribute("books", bookRepository.findAll());
        return "manageBooks";
    }

    // Show the edit book form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home";
        }

        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            return "editBook";
        } else {
            return "redirect:/books/manage";
        }
    }

    // Handle form submission for editing a book
    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home";
        }

        book.setId(id); // Ensure the ID is set correctly
        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        return "redirect:/books/manage";
    }

    // Delete a book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is admin
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return "redirect:/home";
        }

        bookRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        return "redirect:/books/manage";
    }
}