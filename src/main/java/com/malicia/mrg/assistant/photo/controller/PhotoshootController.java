package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.event.PhotoDoneEvent;
import com.malicia.mrg.assistant.photo.event.PhotoEvent;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/photoshoot")
public class PhotoshootController implements ApplicationListener<ApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(PhotoshootController.class);
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MyConfig config;
    private final PhotoshootService photoshootService;
    private final PhotoService photoService;
    private final RootRepertoire rootRepertoire;

    public PhotoshootController(MyConfig config, PhotoshootService photoshootService, PhotoService photoService, RootRepertoire rootRepertoire) {
        this.config = config;
        this.photoshootService = photoshootService;
        this.photoService = photoService;
        this.rootRepertoire = rootRepertoire;
    }

    // 3. Liste des photos d'une Photoshoot
    @GetMapping("/{photoshootTypeName}/{photoshootName}")
    public ResponseEntity<Photoshoot> getPhotoshoot(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        logger.debug("@GetMapping(\"/{photoshootTypeName}/{photoshootName}\")");
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
            @CacheEvict(value = "getPhotoshootList", key = "#photoshootTypeName")

    })
    @GetMapping("/{photoshootTypeName}/{photoshootName}/nocache")
    public ResponseEntity<Photoshoot> getPhotoshootNocache(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        logger.debug("@GetMapping(\"/{photoshootTypeName}/{photoshootName}/nocache\")");
        try {
            PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);
            photoshootService.cleanupPhotoData(photoshootName);
            return ResponseEntity.ok().body(photoshootService.getPhotoshoot(photoshootType, photoshootName));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/{photoshootTypeName}/{photoshootName}/validate")
    public ResponseEntity<ValidationResult> validatePhotoshootName(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        logger.debug("GetMapping(\"/{photoshootTypeName}/{photoshootName}/validate\")");
        PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootType, photoshootName);

        return ResponseEntity.ok(photoshoot.getValidationResult());
    }

    @CacheEvict(value = "getPhotoshootList", key = "#photoshootTypeName")
    @PutMapping("/{photoshootTypeName}/{photoshootName}/rename")
    public ResponseEntity<Map<String, Object>> updateRepertoireName(@PathVariable String photoshootTypeName, @PathVariable String photoshootName, @RequestBody UpdateRepertoireNameRequestDto request) {
        logger.debug("@PutMapping(\"/{photoshootTypeName}/{photoshootName}/rename\")");
        String photoshootNameNew = request.getPhotoshootNameNew();

        PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);

        Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootType, photoshootName);

        String[] photoshootNameNewParts = photoshootNameNew.split("_");
        ValidationResult validationResult = photoshootService.validatePhotoshoot(photoshootType, photoshoot, photoshootNameNewParts);

        String result = RootRepertoire.movePhotoshootToNewFolder(photoshoot, photoshoot.getPhotoshootRoot() + photoshootNameNew, config.getDryRun());

        Boolean status = validationResult.isValid() && result.compareTo("done")==0;

        Map<String, Object> response = new HashMap<>();
        response.put("photoshootName", photoshootName);
        response.put("valid", status);
        response.put("photoshootNameNew", photoshootNameNew);
        response.put("message", (validationResult.isValid()?"":validationResult.getMessage() + "\n")
                + (result.compareTo("done")==0 ?"": result + "\n")
                + (status ? "\nRepertoire name updated successfully." : ""));


        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{photoshootTypeName}/{photoshootName}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPhotoshoot(@PathVariable String photoshootTypeName, @PathVariable String photoshootName) {
        logger.debug("@GetMapping(value = \"/{photoshootTypeName}/{photoshootName}/stream\"");
        UUID sessionId = UUID.randomUUID();

        SseEmitter emitter = new SseEmitter(0L); // No timeout
        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> emitters.remove(sessionId));
        emitter.onError((e) -> emitters.remove(sessionId));
        emitters.put(sessionId, emitter);

        new Thread(() -> {
            PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);
            Photoshoot photoshoot = photoshootService.getPhotoshoot(photoshootType, photoshootName, sessionId);

            try {
                emitter.send(SseEmitter.event()
                        .name("photoshootStatus")
                        .data(photoshoot.getValidationResult()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }

            try {
                emitter.send(SseEmitter.event()
                        .name("endConnection")
                        .data("end of thread"));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }

        }).start();


        return emitter;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof PhotoEvent photoEvent) {
            UUID sessionId = photoEvent.getSessionId();
            SseEmitter emitter = emitters.get(sessionId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("photo")
                            .data(photoEvent.getPhoto()));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }

        if (event instanceof PhotoDoneEvent doneEvent) {
            UUID sessionId = doneEvent.getSessionId();
            SseEmitter emitter = emitters.get(sessionId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("photoDone").data("complete"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }
}
