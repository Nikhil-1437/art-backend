package com.example.artgallery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class OrderDTO {
    // For creating an order
    private List<CartItem> items;

    // For displaying an order
    private Long id;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> orderItems;
    private String buyerName;

    @Data
    public static class CartItem {
        private Long artItemId;
        private int quantity;
    }

    @Data
    public static class OrderItemDTO {
        private String title;
        private int quantity;
        private BigDecimal priceAtPurchase;
        private String imageUrl;
        private String sellerName;
    }
}