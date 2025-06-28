package com.malicia.mrg.assistant.photo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class PhotoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public PhotoDTO() {
    }

    private UUID id;
    private String hash;
    private String path;
    private String relativeToPath;
    private String filename;
    private String extension;
    private String createdDate;
    private String exifDate;
    private String dateTaken;
    private int rating;
    private String label;
    private int pick;
    private List<String> keywords;

    public PhotoDTO(UUID id,
                    String hash,
                    String path,
                    String relativeToPath,
                    String filename,
                    String extension,
                    String createdDate,
                    String exifDate,
                    String dateTaken,
                    int rating,
                    String label,
                    int pick,
                    List<String> keywords) {
        this.id = id;
        this.hash = hash;
        this.path = path;
        this.relativeToPath = relativeToPath;
        this.filename = filename;
        this.extension = extension;
        this.createdDate = createdDate;
        this.exifDate = exifDate;
        this.dateTaken = dateTaken;
        this.rating = rating;
        this.label = label;
        this.pick = pick;
        this.keywords = keywords;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRelativeToPath() {
        return relativeToPath;
    }

    public void setRelativeToPath(String relativeToPath) {
        this.relativeToPath = relativeToPath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExifDate() {
        return exifDate;
    }

    public void setExifDate(String exifDate) {
        this.exifDate = exifDate;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
