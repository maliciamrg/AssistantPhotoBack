package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/photoshoot-type")
public class PhotoshootTypeController {

    private final PhotoshootService photoshootService;

    public PhotoshootTypeController(PhotoshootService photoshootService) {
        this.photoshootService = photoshootService;
    }

    // 1. Liste des types de Photoshoot
    @GetMapping
    public List<PhotoshootType> getPhotoshootType() {
        return photoshootService.getPhotoshootType();
    }

    @GetMapping("{photoshootTypeName}")
    public PhotoshootType getPhotoshootParam(@PathVariable String photoshootTypeName) {
        return photoshootService.getPhotoshootType(photoshootTypeName);
    }

    // 2. Liste des Photoshoot par type de Photoshoot
    @GetMapping("/{photoshootTypeName}/photoshootlist")
    public List<Photoshoot> getPhotoshootByType(@PathVariable String photoshootTypeName) {
        PhotoshootType photoshootType = photoshootService.getPhotoshootType(photoshootTypeName);
        return photoshootService.getPhotoshootList(photoshootType);
    }


}




