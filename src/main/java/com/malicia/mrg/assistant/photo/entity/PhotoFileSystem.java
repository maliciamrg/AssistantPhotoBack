package com.malicia.mrg.assistant.photo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "photo_filesystem")
public class PhotoFileSystem {

    @Id
    @GeneratedValue
    private UUID id;
    private String rootDir;
    private String path;
    private String relativeToPath;
    private String filename;
    private String extension;
    private String createdDate;
    @Column(name = "size_mb")
    private double sizeMB;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photo photo;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
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

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "PhotoFileSystem{" +
                "id=" + id +
                ", rootDir='" + rootDir + '\'' +
                ", path='" + path + '\'' +
                ", relativeToPath='" + relativeToPath + '\'' +
                ", filename='" + filename + '\'' +
                ", extension='" + extension + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", photoId=" + (photo != null ? photo.getId() : null) +
                '}';
    }

    public void setSizeMB(double sizeMB) {
        this.sizeMB = sizeMB;
    }

    public double getSizeMB() {
        return sizeMB;
    }

    // Getters/setters
}
