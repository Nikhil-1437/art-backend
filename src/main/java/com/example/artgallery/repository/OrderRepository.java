package com.example.artgallery.repository;

import com.example.artgallery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.artItem.seller.id = :sellerId")
    List<Order> findOrdersBySellerId(Long sellerId);
}