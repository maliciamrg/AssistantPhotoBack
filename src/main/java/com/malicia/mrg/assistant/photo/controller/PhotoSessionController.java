package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.AssistantPhotoApplication;
import com.malicia.mrg.assistant.photo.DTO.SeanceTypeDto;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
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

    private Duration ttl = Duration.ofMinutes(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MyConfig config;
    private final RootRepertoire rootRep;
    public PhotoSessionController(MyConfig config, RootRepertoire rootRep, RedisTemplate<String, Object> redisTemplate) {
        this.config = config;
        this.rootRep = rootRep;
        this.redisTemplate = redisTemplate;
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
        List<SeanceRepertoire> seanceRepertoire = getSeanceRepertoireList(typeName);
        return seanceRepertoire;
    }

    // 3. Liste des photos d'une séance
    @GetMapping("/{typeName}/{seanceId}")
    public ResponseEntity<List<Photo>> getPhotosDeSeance(@PathVariable String typeName, @PathVariable String seanceId) {
        try {
            List<SeanceRepertoire> seanceList = getSeanceRepertoireList(typeName);

            List<Photo> allPhotoFromPhotoRepertoire = getAllPhotoFromPhotoRepertoire(seanceId, seanceList);
            return ResponseEntity.ok(allPhotoFromPhotoRepertoire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    private List<Photo> getAllPhotoFromPhotoRepertoire(String seanceId, List<SeanceRepertoire> seanceList) {
        List<Photo> cachedPhotos = (List<Photo>) redisTemplate.opsForValue().get(seanceId);
        if (cachedPhotos != null) {
            Long ttl = redisTemplate.getExpire(seanceId);
            logger.info("TTL for key '{}' is: {} seconds", seanceId, ttl);
            return cachedPhotos;
        }
        Optional<SeanceRepertoire> pathToScan = seanceList.stream()
                .filter(seance -> seance.getId().equals(seanceId)) // Filter by id
                .findFirst(); // Return the first match (Optional)
        List<Photo> allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoRepertoire(pathToScan.get());
        redisTemplate.opsForValue().set(seanceId, allPhotoFromPhotoRepertoire,ttl);
        logger.info("redisTemplate.opsForValue().set :" + seanceId);
        return allPhotoFromPhotoRepertoire;
    }

    private List<SeanceRepertoire> getSeanceRepertoireList(String typeName) {
        List<SeanceRepertoire> cachedSeances = (List<SeanceRepertoire>) redisTemplate.opsForValue().get(typeName);
        if (cachedSeances != null) {
            Long ttl = redisTemplate.getExpire(typeName);
            logger.info("TTL for key '{}' is: {} seconds", typeName, ttl);
            return cachedSeances;
        }
        Optional<SeanceTypeEnum> type = config.getSeanceType().stream()
                .map(seanceType -> seanceType.getNom())
                .filter(seanceTypeEnum -> seanceTypeEnum.toString().equals(typeName))
                .findFirst();
        List<SeanceRepertoire> seanceList = rootRep.getAllSeanceRepertoire(type.get());
        redisTemplate.opsForValue().set(typeName, seanceList,ttl);
        logger.info("redisTemplate.opsForValue().set :" + typeName);
        return seanceList;
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
