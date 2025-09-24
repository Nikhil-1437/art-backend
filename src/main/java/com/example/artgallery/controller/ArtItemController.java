package com.example.artgallery.controller;

import com.example.artgallery.dto.ArtItemDTO;
import com.example.artgallery.service.ArtItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/art")
public class ArtItemController {
    private final ArtItemService artItemService;

    public ArtItemController(ArtItemService artItemService) {
        this.artItemService = artItemService;
    }

    @GetMapping
    public List<ArtItemDTO> getAllArtItems() {
        return artItemService.getAllArtItems();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArtItemDTO> getArtItemById(@PathVariable Long id) {
        ArtItemDTO artItemDTO = artItemService.getArtItemById(id);
        if (artItemDTO != null) {
            return ResponseEntity.ok(artItemDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}