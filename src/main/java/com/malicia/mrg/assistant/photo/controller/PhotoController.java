package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.XMPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PhotoController {

    final RootRepertoire rootRep;
    private final PhotoService photoService;
    private final XMPService xmpService;

    public PhotoController(RootRepertoire rootRep, PhotoService photoService, XMPService xmpService) {
        this.rootRep = rootRep;
        this.photoService = photoService;
        this.xmpService = xmpService;
    }


    @PostMapping("/api/photos/batch-update")
    public ResponseEntity<Map<String, String>> index(@RequestBody List<Photo> photos) {
        try {
            photoService.saveAllPhotos(photos,true);

            // Create a map to store the response data
            Map<String, String> response = new HashMap<>();
            response.put("photoCount", String.valueOf(photos.size()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }

}