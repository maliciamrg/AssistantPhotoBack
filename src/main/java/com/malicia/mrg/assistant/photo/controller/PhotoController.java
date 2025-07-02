package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/batch-update")
    public ResponseEntity<Map<String, String>> batchUpdate(@RequestBody PhotoGroup photoGroup) {
        try {
            photoService.saveAllPhotos(photoGroup, true);

            // Create a map to store the response data
            Map<String, String> response = new HashMap<>();
            response.put("photoCount", String.valueOf(photoGroup.size()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid PhotoshootTypeEnum
        }
    }

}