package com.malicia.mrg.assistant.photo.repository;


import com.malicia.mrg.assistant.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    // Example: find photo by hash (useful for checking duplicates)
    Optional<Photo> findByHash(String hash);
}
