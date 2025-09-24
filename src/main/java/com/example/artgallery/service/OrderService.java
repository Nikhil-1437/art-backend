package com.example.artgallery.service;

import com.example.artgallery.dto.OrderDTO;
import com.example.artgallery.model.ArtItem;
import com.example.artgallery.model.Order;
import com.example.artgallery.model.OrderItem;
import com.example.artgallery.model.User;
import com.example.artgallery.repository.ArtItemRepository;
import com.example.artgallery.repository.OrderRepository;
import com.example.artgallery.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ArtItemRepository artItemRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ArtItemRepository artItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.artItemRepository = artItemRepository;
    }

    @Transactional
    public Order createOrder(OrderDTO.CartItem[] cartItems, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + buyerEmail));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(Instant.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDTO.CartItem itemDto : cartItems) {
            ArtItem artItem = artItemRepository.findById(itemDto.getArtItemId())
                    .orElseThrow(() -> new RuntimeException("ArtItem not found: " + itemDto.getArtItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setArtItem(artItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPriceAtPurchase(artItem.getPrice());
            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(artItem.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public List<OrderDTO> getOrdersForBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId).stream()
                .map(this::convertOrderToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersForSeller(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId).stream()
            .map(this::convertOrderToDto)
            .collect(Collectors.toList());
    }


    private OrderDTO convertOrderToDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setBuyerName(order.getBuyer().getName());

        List<OrderDTO.OrderItemDTO> itemDtos = order.getItems().stream().map(orderItem -> {
            OrderDTO.OrderItemDTO itemDto = new OrderDTO.OrderItemDTO();
            itemDto.setTitle(orderItem.getArtItem().getTitle());
            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setPriceAtPurchase(orderItem.getPriceAtPurchase());
            itemDto.setImageUrl(orderItem.getArtItem().getImageUrl());
            itemDto.setSellerName(orderItem.getArtItem().getSeller().getName());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        return dto;
    }

}