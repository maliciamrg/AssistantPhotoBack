package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    final RootRepertoire rootRep;
    private final PhotoService photoService;

    public PhotoController(RootRepertoire rootRep, PhotoService photoService) {
        this.rootRep = rootRep;
        this.photoService = photoService;
    }


    @PostMapping("/batch-update")
    public ResponseEntity<Map<String, String>> batchUpdate(@RequestBody PhotoGroup photos) {
        try {
            photoService.saveAllPhotos(photos,true);

            // Create a map to store the response data
            Map<String, String> response = new HashMap<>();
            response.put("photoCount", String.valueOf(photos.size()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid PhotoshootTypeEnum
        }
    }

}