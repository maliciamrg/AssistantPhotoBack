package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import org.springframework.stereotype.Service;

@Service
public class PhotoCleanupService {

    private final PhotoRepository photoRepository;

    public PhotoCleanupService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public int deletePhotosWithDuplicateHash() {
        return photoRepository.deletePhotosWithDuplicateHash();
    }

    public void cleanupOrphanedPhotoData() {
        photoRepository.cleanupOrphanedPhotoData();
    }

    public void cleanupAllPhotoData() {
        photoRepository.cleanupAllPhotoData();
    }

}