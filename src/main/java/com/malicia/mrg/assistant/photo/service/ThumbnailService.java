package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.entity.PhotoThumbnail;
import com.malicia.mrg.assistant.photo.repository.PhotoThumbnailRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ThumbnailService {
    private final PhotoThumbnailRepository photoThumbnailRepository;

    public ThumbnailService(PhotoThumbnailRepository photoThumbnailRepository) {
        this.photoThumbnailRepository = photoThumbnailRepository;
    }

    public PhotoThumbnail getThumbnail(String photoUUID) {

        Optional<PhotoThumbnail> existingPhotoThumbnail = photoThumbnailRepository.findByPhotoId(UUID.fromString(photoUUID));
        if (existingPhotoThumbnail.isPresent()) {
            return existingPhotoThumbnail.get();
        }
        return new PhotoThumbnail();
    }
}
