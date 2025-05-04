package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController


@RequestMapping("/api/database")
public class DatabaseController {

    private final PhotoService photoService;
    private final RootRepertoire rootRep;

    public DatabaseController(PhotoService photoService, RootRepertoire rootRep) {
        this.photoService = photoService;
        this.rootRep = rootRep;
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> index(@RequestBody SeanceRepertoire SeanceRepertoire) {
        try {
            List<Photo> allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoRepertoire(SeanceRepertoire);
            photoService.saveAllPhotos(allPhotoFromPhotoRepertoire, false);

            // Create a map to store the response data
            Map<String, String> response = new HashMap<>();
            response.put("photoCount", String.valueOf(allPhotoFromPhotoRepertoire.size()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }

    @GetMapping("/update/by-seance-type")
    public ResponseEntity<Map<String, String>> index(@RequestParam SeanceTypeEnum seanceType) {
        try {

            List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(seanceType);
            List<Photo> allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoire(assistantRepertoire);
            photoService.saveAllPhotos(allPhotoFromSeanceRepertoire, false);

            // Create a map to store the response data
            Map<String, String> response = new HashMap<>();
            response.put("photoCount", String.valueOf(allPhotoFromSeanceRepertoire.size()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid SeanceTypeEnum
        }
    }


}
