package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.dto.PhotoMetadataDTO;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    // GET all photos
    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(photoService.getAllPhotos());
    }

    // GET photo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable UUID id) {
        return photoService.getPhotoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create photo
    @PostMapping
    public ResponseEntity<Photo> createPhoto(@RequestBody Photo photo) {
        Photo createdPhoto = photoService.savePhoto(photo);
        return ResponseEntity.ok(createdPhoto);
    }

    // PUT update photo
    @PutMapping("/{id}")
    public ResponseEntity<Photo> updatePhoto(@PathVariable UUID id, @RequestBody Photo photo) {
        return photoService.updatePhoto(id, photo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE photo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID id) {
        if (photoService.deletePhoto(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<Void> updatePhotoMetadata(@PathVariable UUID id, @RequestBody PhotoMetadataDTO metadataDTO) {
        photoService.updatePhotoMetadata(id, metadataDTO);
        return ResponseEntity.ok().build();
    }


}
