package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.service.PhotoCleanupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class PhotoMaintenanceController {

    private final PhotoCleanupService photoCleanupService;

    public PhotoMaintenanceController(PhotoCleanupService photoCleanupService) {
        this.photoCleanupService = photoCleanupService;
    }

    @DeleteMapping("/photos/duplicates")
    public ResponseEntity<Map<String, Object>> deletePhotosWithDuplicateHash() {
        int deletedCount = photoCleanupService.deletePhotosWithDuplicateHash();

        Map<String, Object> result = Map.of(
                "deletedCount", deletedCount,
                "message", "Photos with duplicate hashes removed"
        );

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/photos/orphans")
    public ResponseEntity<Map<String, Object>> deleteOrphanedPhotoData() {
        photoCleanupService.cleanupOrphanedPhotoData();
        return ResponseEntity.ok(Map.of(
                "message", "Orphaned photo-related records cleaned up"
        ));
    }
}
