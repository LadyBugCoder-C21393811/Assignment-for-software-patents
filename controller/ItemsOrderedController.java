package com.example.onlinebookshop.controller;

import com.example.onlinebookshop.model.ItemsOrdered;
import com.example.onlinebookshop.model.Order;
import com.example.onlinebookshop.model.Book;
import com.example.onlinebookshop.repository.ItemsOrderedRepository;
import com.example.onlinebookshop.repository.OrderRepository;
import com.example.onlinebookshop.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemsOrderedController {

    @Autowired
    private ItemsOrderedRepository itemsOrderedRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<ItemsOrdered> getAllItemsOrdered() {
        return itemsOrderedRepository.findAll();
    }

    @PostMapping("/add")
    public ItemsOrdered addItemToOrder(@RequestParam Long orderId,
                                       @RequestParam Long bookId,
                                       @RequestParam int quantity) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (orderOpt.isPresent() && bookOpt.isPresent()) {
            ItemsOrdered item = new ItemsOrdered(bookOpt.get(), orderOpt.get(), quantity);
            return itemsOrderedRepository.save(item);
        } else {
            throw new RuntimeException("Order or Book not found");
        }
    }
}
