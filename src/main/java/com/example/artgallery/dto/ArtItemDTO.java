package com.example.artgallery.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ArtItemDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Long sellerId;
    private String sellerName;
}