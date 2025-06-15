package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.SeanceTypeDto;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.pojo.MetaDataRep;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class PhotoSessionController {

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
                    return new SeanceTypeDto(
                            seanceTypeEnum.name(),
                            seanceTypeEnum.toString(),
                            seanceType.getUniteDeJour(),
                            seanceType.getNbMaxParUniteDeJour(),
                            seanceType.getRatioStarMax(),
                            seanceType.getZoneValeurAdmise()
                    )
                            ;
                })
                .toList();
    }

    // 2. Liste des séances par type de séance
    @GetMapping("/{typeName}")
    public List<SeanceRepertoire> getSeancesParType(@PathVariable String typeName) {
        return photoSessionService.getSeanceRepertoireList(typeName);
    }

    // 3. Liste des photos d'une séance
    @GetMapping("/{typeName}/{seanceId}")
    public ResponseEntity<Map<String, Object>> getPhotosDeSeance(@PathVariable String typeName, @PathVariable String seanceId) {
        try {
            List<SeanceRepertoire> seanceList = photoSessionService.getSeanceRepertoireList(typeName);

            List<Photo> allPhotoFromPhotoRepertoire = photoSessionService.getAllPhotoFromPhotoRepertoire(seanceId, seanceList);

            MetaDataRep metaDataFromPhotoRepertoire = photoSessionService.getMetaDataFromPhotoRepertoire(seanceId, seanceList);

            Map<String, Object> response = new HashMap<>();
            response.put("photos", allPhotoFromPhotoRepertoire);
            response.put("metadata", metaDataFromPhotoRepertoire);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
