package com.malicia.mrg.assistant.photo.repository;

import com.malicia.mrg.assistant.photo.repertoire.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findByPathAndFilename(String path, String filename);
    List<Photo> findByPathStartingWith(String prefix);
}
