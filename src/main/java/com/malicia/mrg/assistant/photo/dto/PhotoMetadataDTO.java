package com.malicia.mrg.assistant.photo.dto;

import java.io.Serializable;
import java.util.List;

public class PhotoMetadataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rating;
    private int pick;
    private String label;
    private List<String> keywords;

    public PhotoMetadataDTO() {
    }

    public PhotoMetadataDTO(int rating, int pick, String label, List<String> keywords) {
        this.rating = rating;
        this.pick = pick;
        this.label = label;
        this.keywords = keywords;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
