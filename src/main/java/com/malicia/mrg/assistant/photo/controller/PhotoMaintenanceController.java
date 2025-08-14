package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.service.PhotoCleanupService;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class PhotoMaintenanceController {

    private final PhotoCleanupService photoCleanupService;
    private final TagService tagService;

    public PhotoMaintenanceController(PhotoCleanupService photoCleanupService, TagService tagService) {
        this.photoCleanupService = photoCleanupService;
        this.tagService = tagService;
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

    @DeleteMapping("/photos/purgeAll")
    public ResponseEntity<Map<String, Object>> deleteAllPhotoData() {
        photoCleanupService.cleanupAllPhotoData();
        return ResponseEntity.ok(Map.of(
                "message", "Purge all records"
        ));
    }

    @DeleteMapping("/photos/purgeAll/{photoshootName}")
    public ResponseEntity<Map<String, Object>> deleteAllPhotoData(@PathVariable String photoshootName) {
        photoCleanupService.cleanupAllPhotoData(photoshootName);
        return ResponseEntity.ok(Map.of(
                "message", "Purge all records of " + photoshootName
        ));
    }

    @PostMapping("/normalize-names")
    public ResponseEntity<String> normalizeTagNames() {
        tagService.normalizeAllTagNames();
        return ResponseEntity.ok("All tag names normalized successfully.");
    }
}
