package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.entity.PhotoThumbnail;
import com.malicia.mrg.assistant.photo.service.ThumbnailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thumbnails")
public class ThumbnailController {
    private final ThumbnailService thumbnailService;

    public ThumbnailController(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @GetMapping("/{photoUUID}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String photoUUID) {
        PhotoThumbnail thumbnail = thumbnailService.getThumbnail(photoUUID);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(thumbnail.getData());
    }

}
