package com.malicia.mrg.assistant.photo.entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "photos")
public class Photo implements Serializable {
    public static final String T_09_15_00_Z = "2024-03-12T09:15:00Z";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private UUID id;  // Will use UUID.randomUUID() or Hibernate strategy
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


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "photo_keywords", joinColumns = @JoinColumn(name = "photo_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PhotoThumbnail thumbnail;

    public Photo() {
    }

    public PhotoThumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(PhotoThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    // Getters and setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        if (dateTaken == null || dateTaken.compareTo(T_09_15_00_Z) == 0) {
            this.dateTaken = createdDate;
        }
    }

    public String getExifDate() {
        return exifDate;
    }

    public void setExifDate(String exifDate) {
        this.exifDate = exifDate;
        this.dateTaken = exifDate;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "path='" + path + '\'' +
                ", relativeToPath='" + relativeToPath + '\'' +
                ", filename='" + filename + '\'' +
                ", extension='" + extension + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", exifDate='" + exifDate + '\'' +
                '}';
    }

    public String getRelativeToPath() {
        return relativeToPath;
    }

    public void setRelativeToPath(String relativeToPath) {
        this.relativeToPath = relativeToPath;
    }

}
