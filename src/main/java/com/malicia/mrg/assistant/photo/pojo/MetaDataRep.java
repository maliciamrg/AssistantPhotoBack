package com.malicia.mrg.assistant.photo.pojo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;

import java.util.Map;

public class MetaDataRep {
    private SeanceRepertoire seanceRepertoire;
    private int nbPhotoTotal;
    private int nbNotSelectedPhoto;
    private int nbSelectedPhoto;
    private int nbRejectedPhoto;
    private String lowerDate;
    private String upperDate;
    private long nbDay;
    private int[] nbStar;
    private Map<String, Integer> nbLabel;
    private Map<String, Integer> nbTag;
    private String repertoireName;
    private String[] repertoireNameParts;

    // Getters and Setters
    public SeanceRepertoire getSeanceRepertoire() {
        return seanceRepertoire;
    }

    public void setSeanceRepertoire(SeanceRepertoire seanceRepertoire) {
        this.seanceRepertoire = seanceRepertoire;
    }

    public int getNbPhotoTotal() {
        return nbPhotoTotal;
    }

    public void setNbPhotoTotal(int nbPhotoTotal) {
        this.nbPhotoTotal = nbPhotoTotal;
    }

    public int getNbNotSelectedPhoto() {
        return nbNotSelectedPhoto;
    }

    public void setNbNotSelectedPhoto(int nbNotSelectedPhoto) {
        this.nbNotSelectedPhoto = nbNotSelectedPhoto;
    }

    public int getNbSelectedPhoto() {
        return nbSelectedPhoto;
    }

    public void setNbSelectedPhoto(int nbSelectedPhoto) {
        this.nbSelectedPhoto = nbSelectedPhoto;
    }

    public int getNbRejectedPhoto() {
        return nbRejectedPhoto;
    }

    public void setNbRejectedPhoto(int nbRejectedPhoto) {
        this.nbRejectedPhoto = nbRejectedPhoto;
    }

    public String getLowerDate() {
        return lowerDate;
    }

    public void setLowerDate(String lowerDate) {
        this.lowerDate = lowerDate;
    }

    public String getUpperDate() {
        return upperDate;
    }

    public void setUpperDate(String upperDate) {
        this.upperDate = upperDate;
    }

    public long getNbDay() {
        return nbDay;
    }

    public void setNbDay(long nbDay) {
        this.nbDay = nbDay;
    }

    public int[] getNbStar() {
        return nbStar;
    }

    public void setNbStar(int[] nbStar) {
        this.nbStar = nbStar;
    }

    public Map<String, Integer> getNbLabel() {
        return nbLabel;
    }

    public void setNbLabel(Map<String, Integer> nbLabel) {
        this.nbLabel = nbLabel;
    }

    public Map<String, Integer> getNbTag() {
        return nbTag;
    }

    public void setNbTag(Map<String, Integer> nbTag) {
        this.nbTag = nbTag;
    }

    public String getRepertoireName() {
        return repertoireName;
    }

    public void setRepertoireName(String repertoireName) {
        this.repertoireName = repertoireName;
    }

    public String[] getRepertoireNameParts() {
        return repertoireNameParts;
    }

    public void setRepertoireNameParts(String[] repertoireNameParts) {
        this.repertoireNameParts = repertoireNameParts;
    }
}
