package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.Book;
import com.example.onlinebookshop.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // View all books (for customers)
    @GetMapping("/view")
    public String viewBooks(Model model, @RequestParam(required = false) String sortBy,
                            @RequestParam(required = false) String sortDir) {

        List<Book> books;

        // Handle sorting if parameters are provided
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortDir != null && sortDir.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            books = bookRepository.findAll(Sort.by(direction, sortBy));
        } else {
            books = bookRepository.findAll();
        }

        model.addAttribute("books", books);
        return "viewBooks";
    }

    // Search books by title
    @GetMapping("/search")
    public String searchBooks(@RequestParam String title, Model model) {
        // This assumes you've created a findByTitleContainingIgnoreCase method in your repository
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        model.addAttribute("books", books);
        model.addAttribute("searchTerm", title);
        return "viewBooks";
    }

    // View book details
    @GetMapping("/details/{id}")
    public String viewBookDetails(@PathVariable Long id, Model model) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            return "bookDetails";
        } else {
            return "redirect:/books/view";
        }
    }
}