package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
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
            return ResponseEntity.ok().body(photoshootService.getPhotoshoot(photoshootTypeName,photoshootName));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/{photoshootTypeName}/{photoshootName}/validate")
    public ResponseEntity<ValidationResult> validatePhotoshootName(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootTypeName, photoshootName);

        return ResponseEntity.ok(photoshootService.validatePhotoshoot(photoshootTypeName , photoshoot));
    }

    @PutMapping("/{photoshootTypeName}/{photoshootName}/rename")
    public ResponseEntity<Map<String, Object>> updateRepertoireName(@PathVariable String photoshootTypeName,@PathVariable String photoshootName, @RequestBody UpdateRepertoireNameRequestDto request) {

        String photoshootNameNew = request.getPhotoshootNameNew();

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootTypeName, photoshootName);

        String[] photoshootNameNewParts = photoshootNameNew.split("_");
        ValidationResult validationResult = photoshootService.validatePhotoshoot(photoshootTypeName , photoshoot, photoshootNameNewParts);

        RootRepertoire.moveGroupToDestinationFolder(config.getRootPath() + photoshootNameNew, photoshoot.getGroupOfPhoto(), false, config.getDryRun());

        Map<String, Object> response = new HashMap<>();
        response.put("photoshootName", photoshootName);
        response.put("valid", validationResult.isValid());
        response.put("photoshootNameNew", photoshootNameNew);
        response.put("message", validationResult.getMessage() + (validationResult.isValid() ? "\nRepertoire name updated successfully." : ""));


        return ResponseEntity.ok(response);
    }
}
