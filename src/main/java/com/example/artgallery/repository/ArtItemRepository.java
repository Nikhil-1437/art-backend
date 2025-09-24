package com.example.artgallery.repository;

import com.example.artgallery.model.ArtItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtItemRepository extends JpaRepository<ArtItem, Long> {
    List<ArtItem> findBySellerId(Long sellerId);
}