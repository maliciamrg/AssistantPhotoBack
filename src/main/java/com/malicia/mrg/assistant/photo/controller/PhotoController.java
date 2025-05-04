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

    @PostMapping("/api/photos")
    public ResponseEntity<List<Photo>> index(@RequestBody SeanceRepertoire SeanceRepertoire) {
        try {
            String pathToScan = SeanceRepertoire.getPath();
            List<Photo> allPhotoFromPhotoRepertoire = photoService.getAllPhotosWithPathStartingWith(pathToScan);
            return ResponseEntity.ok(allPhotoFromPhotoRepertoire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }

    @GetMapping("/api/photos/by-seance-type")
    public ResponseEntity<List<Photo>> index(@RequestParam SeanceTypeEnum seanceType) {
        try {
            List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(seanceType);
            List<String> allPathFromSeanceRepertoire = rootRep.getAllPathFromSeanceRepertoire(assistantRepertoire);
            List<Photo> allPhotoFromSeanceRepertoire = new ArrayList<>();
            for (String pathToScan : allPathFromSeanceRepertoire) {
                allPhotoFromSeanceRepertoire.addAll(photoService.getAllPhotosWithPathStartingWith(pathToScan));
            }
            return ResponseEntity.ok(allPhotoFromSeanceRepertoire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
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

    @GetMapping("/api/photo/{id}")
    public ResponseEntity<Photo> index(@PathVariable UUID id) {
        try {
            Photo photFromId = photoService.getPhotoById(id);
            return ResponseEntity.ok(photFromId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }

    @PatchMapping("/api/photo/{id}")
    public ResponseEntity<Photo> index(@PathVariable UUID id, @RequestBody Photo photo) {
        try {
            Photo photFromId = photoService.getPhotoById(id);
            List<Photo> photFromIdMod = new ArrayList<>();
            photFromId.mergeFrom(photo);
            photFromIdMod.add(photFromId);
            photoService.saveAllPhotos(photFromIdMod, false);
            return ResponseEntity.ok(photFromId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }
}