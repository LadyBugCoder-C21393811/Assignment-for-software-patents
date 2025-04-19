
package com.example.onlinebookshop.repository;

import com.example.onlinebookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find books by title (case-insensitive, partial match)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Find books by author (case-insensitive, partial match)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Find books by genre (case-insensitive, exact match)
    List<Book> findByGenreIgnoreCase(String genre);

    // Find books by price range
    List<Book> findByPriceBetween(double minPrice, double maxPrice);

    // Find books with stock available
    List<Book> findByQuantityGreaterThan(int minQuantity);
}