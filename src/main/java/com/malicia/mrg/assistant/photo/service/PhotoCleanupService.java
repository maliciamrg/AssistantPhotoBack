package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import org.springframework.stereotype.Service;

@Service
public class PhotoCleanupService {

    private final PhotoRepository photoRepository;
    private final PhotoService photoService;

    public PhotoCleanupService(PhotoRepository photoRepository, PhotoService photoService) {
        this.photoRepository = photoRepository;
        this.photoService = photoService;
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

    public void cleanupAllPhotoData(String photoshootName) {
        photoService.removeAllPhotoData(photoshootName);
    }
}