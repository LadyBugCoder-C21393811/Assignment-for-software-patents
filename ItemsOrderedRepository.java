package com.example.onlinebookshop.repository;

import com.example.onlinebookshop.model.ItemsOrdered;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsOrderedRepository extends JpaRepository<ItemsOrdered, Long> {

}
