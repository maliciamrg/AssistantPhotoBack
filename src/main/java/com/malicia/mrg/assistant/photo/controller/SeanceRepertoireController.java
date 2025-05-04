package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seance-repertoire")
public class SeanceRepertoireController {

    final
    RootRepertoire rootRep;

    public SeanceRepertoireController(RootRepertoire rootRep) {
        this.rootRep = rootRep;
    }

    @GetMapping
    public List<SeanceRepertoire> getSeanceRepertoires(@RequestParam("type") SeanceTypeEnum type) {
        return rootRep.getAllSeanceRepertoire(type);
    }
}
