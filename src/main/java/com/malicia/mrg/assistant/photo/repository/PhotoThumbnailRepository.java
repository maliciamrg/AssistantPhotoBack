package com.malicia.mrg.assistant.photo.repository;


import com.malicia.mrg.assistant.photo.entity.PhotoThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhotoThumbnailRepository extends JpaRepository<PhotoThumbnail, UUID> {
    Optional<PhotoThumbnail> findByPhotoId(UUID uuid);
}

