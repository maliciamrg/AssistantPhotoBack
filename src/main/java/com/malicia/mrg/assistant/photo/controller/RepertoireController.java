package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.pojo.ShootingParam;
import com.malicia.mrg.assistant.photo.repertoire.GroupOfPhotos;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import com.malicia.mrg.assistant.photo.service.RepertoireService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repertoires")
public class RepertoireController {

    private final MyConfig config;
    private final RepertoireService repertoireService;
    private final PhotoSessionService photoSessionService;

    public RepertoireController(MyConfig config, RepertoireService repertoireService, PhotoSessionService photoSessionService) {
        this.config = config;
        this.repertoireService = repertoireService;
        this.photoSessionService = photoSessionService;
    }

    @GetMapping("/validate/{typeName}/{seanceId}")
    public ResponseEntity<Map<String, Object>> validateRepertoireName(@PathVariable String typeName, @PathVariable String seanceId) {

        ValidationResult validationResult = repertoireService.validateRepertoire(typeName, seanceId);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", validationResult.isValid());
        response.put("message", validationResult.getMessage());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/config/{typeId}")
    public ResponseEntity<ShootingParam> getRepertoireNameConfig(@PathVariable String typeId) {

        ShootingParam shootingParam = repertoireService.getshootingParam(typeId);

        return ResponseEntity.ok(shootingParam);
    }

    @PutMapping("/{typeId}/name")
    public ResponseEntity<Map<String, Object>> updateRepertoireName(@PathVariable String typeId, @RequestBody UpdateRepertoireNameRequestDto request) {

        String[] parts = request.getRepertoireNameNew().split("_");
        ValidationResult validationResult = repertoireService.validateRepertoire(typeId, request.getRepertoireNameOld(), parts);

        // Replace with your actual update logic
        String repertoireName = request.getRepertoireNameOld();
        String updatedName = request.getRepertoireNameNew();

        List<SeanceRepertoire> seanceList = photoSessionService.getSeanceRepertoireList(typeId);
        GroupOfPhotos groupOfPhotos = new GroupOfPhotos(photoSessionService.getAllPhotoFromPhotoRepertoire(repertoireName, seanceList));
        RootRepertoire.moveGroupToDestinationFolder(updatedName, groupOfPhotos, false, config.getDryRun());

        Map<String, Object> response = new HashMap<>();
        response.put("old", repertoireName);
        response.put("valid", validationResult.isValid());
        response.put("repertoireName", updatedName);
        response.put("message", validationResult.getMessage() + (validationResult.isValid() ? "\nRepertoire name updated successfully." : ""));


        return ResponseEntity.ok(response);
    }

}




