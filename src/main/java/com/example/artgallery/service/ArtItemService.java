package com.example.artgallery.service;

import com.example.artgallery.dto.ArtItemDTO;
import com.example.artgallery.model.ArtItem;
import com.example.artgallery.repository.ArtItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtItemService {
    private final ArtItemRepository artItemRepository;

    public ArtItemService(ArtItemRepository artItemRepository) {
        this.artItemRepository = artItemRepository;
    }

    public List<ArtItemDTO> getAllArtItems() {
        return artItemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ArtItemDTO getArtItemById(Long id) {
        return artItemRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    private ArtItemDTO convertToDto(ArtItem artItem) {
        ArtItemDTO dto = new ArtItemDTO();
        dto.setId(artItem.getId());
        dto.setTitle(artItem.getTitle());
        dto.setDescription(artItem.getDescription());
        dto.setPrice(artItem.getPrice());
        dto.setCategory(artItem.getCategory());
        dto.setImageUrl(artItem.getImageUrl());
        if (artItem.getSeller() != null) {
            dto.setSellerId(artItem.getSeller().getId());
            dto.setSellerName(artItem.getSeller().getName());
        }
        return dto;
    }
}