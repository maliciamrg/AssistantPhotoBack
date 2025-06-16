package com.malicia.mrg.assistant.photo.pojo;

import java.util.List;

// Inner Classes for seanceType and seanceRepertoire
public class PhotoshootType {
    private PhotoshootTypeEnum nom;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> ratioStarMin;
    private List<Integer> ratioStarMax;
    private List<String> zoneValeurAdmise;
    private boolean rapprochementNewOk;
    private List<Photoshoot> photoshootList;

    public List<Integer> getRatioStarMin() {
        return ratioStarMin;
    }

    public void setRatioStarMin(List<Integer> ratioStarMin) {
        this.ratioStarMin = ratioStarMin;
    }

    // Getters and Setters
    public PhotoshootTypeEnum getNom() {
        return nom;
    }

    public void setNom(PhotoshootTypeEnum nom) {
        this.nom = nom;
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

    public boolean isRapprochementNewOk() {
        return rapprochementNewOk;
    }

    public void setRapprochementNewOk(boolean rapprochementNewOk) {
        this.rapprochementNewOk = rapprochementNewOk;
    }

    public List<Photoshoot> getPhotoshootList() {
        return photoshootList;
    }

    public void setPhotoshootList(List<Photoshoot> photoshootList) {
        this.photoshootList = photoshootList;
    }
}

