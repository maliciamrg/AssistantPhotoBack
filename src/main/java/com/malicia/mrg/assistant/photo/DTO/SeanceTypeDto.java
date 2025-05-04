package com.malicia.mrg.assistant.photo.DTO;

import java.util.List;

public class SeanceTypeDto {
    private String id;
    private String name;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> ratioStarMax;
    private List<String> zoneValeurAdmise;

    // Default constructor
    public SeanceTypeDto() {
    }

    // Parameterized constructor
    public SeanceTypeDto(String id, String name, int uniteDeJour, int nbMaxParUniteDeJour, List<Integer> ratioStarMax, List<String> zoneValeurAdmise) {
        this.id = id;
        this.name = name;
        this.uniteDeJour = uniteDeJour;
        this.nbMaxParUniteDeJour = nbMaxParUniteDeJour;
        this.ratioStarMax = ratioStarMax;
        this.zoneValeurAdmise = zoneValeurAdmise;
    }

    public int getUniteDeJour() {
        return uniteDeJour;
    }

    public void setUniteDeJour(int uniteDeJour) {
        this.uniteDeJour = uniteDeJour;
    }

    public int getNbMaxParUniteDeJour() {
        return nbMaxParUniteDeJour;
    }

    public void setNbMaxParUniteDeJour(int nbMaxParUniteDeJour) {
        this.nbMaxParUniteDeJour = nbMaxParUniteDeJour;
    }

    public List<Integer> getRatioStarMax() {
        return ratioStarMax;
    }

    public void setRatioStarMax(List<Integer> ratioStarMax) {
        this.ratioStarMax = ratioStarMax;
    }

    public List<String> getZoneValeurAdmise() {
        return zoneValeurAdmise;
    }

    public void setZoneValeurAdmise(List<String> zoneValeurAdmise) {
        this.zoneValeurAdmise = zoneValeurAdmise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

