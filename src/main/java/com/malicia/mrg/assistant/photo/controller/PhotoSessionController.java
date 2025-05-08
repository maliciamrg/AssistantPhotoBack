package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.AssistantPhotoApplication;
import com.malicia.mrg.assistant.photo.DTO.SeanceTypeDto;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
public class PhotoSessionController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoSessionController.class);

    private final MyConfig config;
    private final PhotoSessionService photoSessionService;

    public PhotoSessionController(MyConfig config, PhotoSessionService photoSessionService) {
        this.config = config;
        this.photoSessionService = photoSessionService;
    }

    // 1. Liste des types de séance
    @GetMapping
    public List<SeanceTypeDto> getTypesDeSeance() {
        return config.getSeanceType().stream()
                .map(seanceType -> {
                    SeanceTypeEnum seanceTypeEnum = seanceType.getNom();
                    SeanceTypeDto dto = new SeanceTypeDto(
                            seanceTypeEnum.name(),
                            seanceTypeEnum.toString(),
                            seanceType.getUniteDeJour(),
                            seanceType.getNbMaxParUniteDeJour(),
                            seanceType.getRatioStarMax(),
                            seanceType.getZoneValeurAdmise()
                    )
                            ;
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 2. Liste des séances par type de séance
    @GetMapping("/{typeName}")
    public List<SeanceRepertoire> getSeancesParType(@PathVariable String typeName) {
        List<SeanceRepertoire> seanceRepertoire = photoSessionService.getSeanceRepertoireList(typeName);
        return seanceRepertoire;
    }

    // 3. Liste des photos d'une séance
    @GetMapping("/{typeName}/{seanceId}")
    public ResponseEntity<List<Photo>> getPhotosDeSeance(@PathVariable String typeName, @PathVariable String seanceId) {
        try {
            List<SeanceRepertoire> seanceList = photoSessionService.getSeanceRepertoireList(typeName);

            List<Photo> allPhotoFromPhotoRepertoire = photoSessionService.getAllPhotoFromPhotoRepertoire(seanceId, seanceList);
            return ResponseEntity.ok(allPhotoFromPhotoRepertoire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

//    // 4. Téléchargement d'une photo spécifique
//    @GetMapping("/{type}/{seance}/photos/{photoId}")
//    public ResponseEntity<?> downloadPhoto(@PathVariable String type, @PathVariable String seance, @PathVariable String photoId) {
//        if (!TYPES_DE_SEANCE.contains(type)) {
//            return ResponseEntity.status(404).body("Type de séance non trouvé");
//        }
//
//        // Simulation du téléchargement d'une photo spécifique
//        if ("evenement".equals(type) && "anniversaire_2025".equals(seance) && PHOTOS_ANNIVERSAIRE.contains(photoId)) {
//            // Simuler le téléchargement de la photo
//            return ResponseEntity.ok("Contenu de la photo (photo simulée)");
//        }
//
//        return ResponseEntity.status(404).body("Photo non trouvée");
//    }
//
//    // 5. Ajout d'une photo dans une séance (simulé)
//    @PostMapping("/{type}/{seance}/photos")
//    public ResponseEntity<String> addPhoto(@PathVariable String type, @PathVariable String seance, @RequestBody String photoBase64) {
//        if (!TYPES_DE_SEANCE.contains(type)) {
//            return ResponseEntity.status(404).body("Type de séance non trouvé");
//        }
//
//        // Simuler l'ajout d'une photo (ici, on suppose que la photo est envoyée en base64)
//        return ResponseEntity.ok("Photo ajoutée avec succès dans la séance : " + seance);
//    }
//
//    // 6. Suppression d'une photo
//    @DeleteMapping("/{type}/{seance}/photos/{photoId}")
//    public ResponseEntity<String> deletePhoto(@PathVariable String type, @PathVariable String seance, @PathVariable String photoId) {
//        if (!TYPES_DE_SEANCE.contains(type)) {
//            return ResponseEntity.status(404).body("Type de séance non trouvé");
//        }
//
//        // Simuler la suppression de la photo
//        if ("evenement".equals(type) && "anniversaire_2025".equals(seance) && PHOTOS_ANNIVERSAIRE.contains(photoId)) {
//            return ResponseEntity.ok("Photo supprimée avec succès : " + photoId);
//        }
//
//        return ResponseEntity.status(404).body("Photo non trouvée");
//    }
}
