package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.RepertoireNameValidationRequestDto;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.repertoire.GroupOfPhotos;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repertoires")
public class RepertoireController {

    private final MyConfig config;
    private final TagService tagService;
    private final PhotoSessionService photoSessionService;
    private final RootRepertoire rootRepertoire;

    public RepertoireController(MyConfig config, TagService tagService, PhotoSessionService photoSessionService, RootRepertoire rootRepertoire) {
        this.config = config;
        this.tagService = tagService;
        this.photoSessionService = photoSessionService;
        this.rootRepertoire = rootRepertoire;
    }

    @PostMapping("/validate-name")
    public ResponseEntity<Map<String, Object>> validateRepertoireName(@RequestBody RepertoireNameValidationRequestDto request) {
        String repertoireName = request.getRepertoireName();
        
        ValidationResult validationResult = validateRepertoire(
                request.getTypeName(),
                repertoireName
        );

        Map<String, Object> response = new HashMap<>();
        response.put("valid", validationResult.isValid());
        response.put("message", validationResult.getMessage());

        return ResponseEntity.ok(response);
    }

    
    public ValidationResult validateRepertoire(String typeName, String repertoireName) {
        List<SeanceRepertoire> seanceRepertoireList = photoSessionService.getSeanceRepertoireList(typeName);
        Map<String, Object> metaDataFromPhotoRepertoire = photoSessionService.getMetaDataFromPhotoRepertoire(repertoireName, seanceRepertoireList);
        String[] parts = repertoireName.split("_");
        List<List<String>> possibleValueForRepertoireName = getSubStringPossibleValueForRepertoireName(typeName);

        return controlRepertoire( parts, metaDataFromPhotoRepertoire, possibleValueForRepertoireName);
    }  
    
    public ValidationResult validateRepertoire(String typeName, String repertoireName, String[] parts) {
        List<SeanceRepertoire> seanceRepertoireList = photoSessionService.getSeanceRepertoireList(typeName);
        Map<String, Object> metaDataFromPhotoRepertoire = photoSessionService.getMetaDataFromPhotoRepertoire(repertoireName, seanceRepertoireList);
        List<List<String>> possibleValueForRepertoireName = getSubStringPossibleValueForRepertoireName(typeName);

        return controlRepertoire( parts, metaDataFromPhotoRepertoire, possibleValueForRepertoireName);
    }
    
    private ValidationResult controlRepertoire(String[] parts, Map<String, Object> metaDataFromPhotoRepertoire, List<List<String>> allowedZones) {
        boolean isValid = true;
        StringBuilder message = new StringBuilder();

        //control repertoire name
        if (allowedZones.size() != parts.length) {
            isValid = false;
            message.append(parts.length).append(" champs pour ").append(allowedZones.size()).append(" attendu \n");
        } else {
            for (int i = 0; i < allowedZones.size(); i++) {
                String expected = allowedZones.get(i).get(0);

                if (expected.startsWith("£") && expected.endsWith("£")) {
                    String key = expected.substring(1, expected.length() - 1);

                    if ("DATE".equals(key)) {

                        String expectedDate = String.valueOf(metaDataFromPhotoRepertoire.get("lowerDate"));

                        if (!expectedDate.equals(parts[i])) {
                            isValid = false;
                            message.append("zone ").append(i).append(" : ").append(parts[i])
                                    .append(" non valid for ").append(expected).append(" (")
                                    .append(expectedDate).append(") \n");
                        }

                    } else {
                        isValid = false;
                        message.append("zone ").append(i).append(" : part ").append(expected).append(" non reconnu \n");
                    }
                } else {
                    if (!allowedZones.get(i).contains(parts[i])) {
                        isValid = false;
                        message.append("zone ").append(i).append(" : ").append(parts[i]).append(" non valid \n");
                    }
                }
            }
        }

        //control rules SceanceType


        return new ValidationResult(isValid, isValid ? "valid" : message.toString());
    }


    @GetMapping("/name/config/{typeId}")
    public ResponseEntity<Map<String, Object>> getRepertoireNameConfig(@PathVariable String typeId) {

        List<List<String>> zoneValeurAdmise = getSubStringPossibleValueForRepertoireName(typeId);

        Map<String, Object> ret = new HashMap<>();

        ret.put("ZoneValeurAdmise", zoneValeurAdmise);

        return ResponseEntity.ok(ret);
    }

    private List<List<String>> getSubStringPossibleValueForRepertoireName(String typeId) {
        List<SeanceType> seanceTypeList = config.getSeanceType().stream()
                .filter(seance -> typeId.equals(seance.getNom().name()))
                .collect(Collectors.toList());

        List<List<String>> possibleValueForRepertoireName = new ArrayList<>();

        if (!seanceTypeList.isEmpty()) {

            for (String placeholder : seanceTypeList.get(0).getZoneValeurAdmise()) {

                List<String> possibleValueForRepertoireNameChamp = new ArrayList<>();

                String findString = "";
                String[] parts = placeholder.split("\\|");
                for (String part : parts) {
                    if (part.startsWith("@") && part.length() > 1 && part.endsWith("@")) {
                        findString = part.substring(1, part.length() - 1);
                        possibleValueForRepertoireNameChamp.addAll(tagService.getTagListByName(findString));
                    } else {
                        possibleValueForRepertoireNameChamp.add(part);
                    }
                }

                possibleValueForRepertoireName.add(possibleValueForRepertoireNameChamp);
            }

        }

        return possibleValueForRepertoireName;
    }


    @PutMapping("/{typeId}/name")
    public ResponseEntity<Map<String, Object>> updateRepertoireName(
            @PathVariable String typeId,
            @RequestBody UpdateRepertoireNameRequestDto request) {

        String[] parts = request.getRepertoireNameNew().split("_");
        ValidationResult validationResult = validateRepertoire(
                typeId,
                request.getRepertoireNameOld(),
                parts
        );

        // Replace with your actual update logic
        String repertoireName = request.getRepertoireNameOld();
        String updatedName = request.getRepertoireNameNew();

        List<SeanceRepertoire> seanceList = photoSessionService.getSeanceRepertoireList(typeId);
        GroupOfPhotos groupOfPhotos = new GroupOfPhotos(photoSessionService.getAllPhotoFromPhotoRepertoire(repertoireName, seanceList));
        rootRepertoire.moveGroupToDestinationFolder(updatedName,groupOfPhotos,false,config.getDryRun());

        Map<String, Object> response = new HashMap<>();
        response.put("old", repertoireName);
        response.put("valid", validationResult.isValid());
        response.put("repertoireName", updatedName);
        response.put("message", validationResult.getMessage());


        return ResponseEntity.ok(response);
    }

    public class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

}




