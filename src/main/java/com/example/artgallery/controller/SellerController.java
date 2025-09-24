package com.example.artgallery.controller;

import com.example.artgallery.dto.ArtItemDTO;
import com.example.artgallery.dto.OrderDTO;
import com.example.artgallery.model.ArtItem;
import com.example.artgallery.model.User;
import com.example.artgallery.repository.ArtItemRepository;
import com.example.artgallery.repository.UserRepository;
import com.example.artgallery.service.FileStorageService;
import com.example.artgallery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private ArtItemRepository artItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private OrderService orderService;


    @PostMapping(value = "/art", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createArtItem(@RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("price") BigDecimal price,
                                           @RequestParam("category") String category,
                                           @RequestParam("image") MultipartFile image) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        String filename = fileStorageService.save(image);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();

        ArtItem artItem = new ArtItem();
        artItem.setTitle(title);
        artItem.setDescription(description);
        artItem.setPrice(price);
        artItem.setCategory(category);
        artItem.setSeller(seller);
    artItem.setImageUrl(imageUrl.replace("http://localhost:8085", "")); // Store relative path

        ArtItem savedItem = artItemRepository.save(artItem);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedItem.getId()).toUri();

        return ResponseEntity.created(location).body(savedItem);
    }

    @GetMapping("/art")
    public List<ArtItem> getMyArtItems() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));
        return artItemRepository.findBySellerId(seller.getId());
    }

    @DeleteMapping("/art/{id}")
    public ResponseEntity<?> deleteArtItem(@PathVariable Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        return artItemRepository.findById(id).map(artItem -> {
            if (!artItem.getSeller().getId().equals(seller.getId())) {
                return ResponseEntity.status(403).body("You can only delete your own art items.");
            }
            artItemRepository.delete(artItem);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getSellerOrders() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));
        
        List<OrderDTO> allOrders = orderService.getOrdersForSeller(seller.getId());

        // Filter order items to only show those belonging to the current seller
        List<OrderDTO> sellerSpecificOrders = allOrders.stream().map(orderDto -> {
            List<OrderDTO.OrderItemDTO> filteredItems = orderDto.getOrderItems().stream()
                .filter(item -> item.getSellerName().equals(seller.getName()))
                .collect(Collectors.toList());
            
            if (filteredItems.isEmpty()) {
                return null;
            }
            orderDto.setOrderItems(filteredItems);
            return orderDto;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());


        return ResponseEntity.ok(sellerSpecificOrders);
    }
}