package com.malicia.mrg.assistant.photo.parameter;

import java.util.List;

// Inner Classes for seanceType and seanceRepertoire
public class SeanceType {
    private SeanceTypeEnum nom;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> ratioStarMax;
    private List<String> zoneValeurAdmise;
    private boolean rapprochementNewOk;

    // Getters and Setters
    public SeanceTypeEnum  getNom() {
        return nom;
    }

    public void setNom(SeanceTypeEnum  nom) {
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

}

