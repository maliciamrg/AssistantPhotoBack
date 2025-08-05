package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/photoshoot")
public class PhotoshootController {

    private final MyConfig config;
    private final PhotoshootService photoshootService;

    public PhotoshootController(MyConfig config, PhotoshootService photoshootService) {
        this.config = config;
        this.photoshootService = photoshootService;
    }

    // 3. Liste des photos d'une Photoshoot
    @GetMapping("/{photoshootTypeName}/{photoshootName}")
    public ResponseEntity<Photoshoot> getPhotoshoot(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        try {
            PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);
            return ResponseEntity.ok().body(photoshootService.getPhotoshoot(photoshootType, photoshootName));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // 3. Liste des photos d'une Photoshoot
    @Caching(evict = {
            @CacheEvict(value = "getAllPhotoFromPhotoshoot", key = "#photoshootName"),
            @CacheEvict(value = "getPhotoshootType", key = "#photoshootTypeName"),
            @CacheEvict(value = "getPhotoshootList", key ="#photoshootTypeName")

    })
    @GetMapping("/{photoshootTypeName}/{photoshootName}/nocache")
    public ResponseEntity<Photoshoot> getPhotoshootNocache(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        try {
            PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);
            return ResponseEntity.ok().body(photoshootService.getPhotoshoot(photoshootType, photoshootName));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/{photoshootTypeName}/{photoshootName}/validate")
    public ResponseEntity<ValidationResult> validatePhotoshootName(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {

        PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootType, photoshootName);

        return ResponseEntity.ok(photoshootService.validatePhotoshoot(photoshootType, photoshoot));
    }

    @PutMapping("/{photoshootTypeName}/{photoshootName}/rename")
    public ResponseEntity<Map<String, Object>> updateRepertoireName(@PathVariable String photoshootTypeName, @PathVariable String photoshootName, @RequestBody UpdateRepertoireNameRequestDto request) {

        String photoshootNameNew = request.getPhotoshootNameNew();

        PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootType, photoshootName);

        String[] photoshootNameNewParts = photoshootNameNew.split("_");
        ValidationResult validationResult = photoshootService.validatePhotoshoot(photoshootType, photoshoot, photoshootNameNewParts);

        RootRepertoire.moveGroupToDestinationFolder(config.getRootPath() + photoshootNameNew, photoshoot.getGroupOfPhoto(), false, config.getDryRun());

        Map<String, Object> response = new HashMap<>();
        response.put("photoshootName", photoshootName);
        response.put("valid", validationResult.isValid());
        response.put("photoshootNameNew", photoshootNameNew);
        response.put("message", validationResult.getMessage() + (validationResult.isValid() ? "\nRepertoire name updated successfully." : ""));


        return ResponseEntity.ok(response);
    }

}
