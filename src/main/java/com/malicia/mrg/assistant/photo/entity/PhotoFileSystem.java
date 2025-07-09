package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;

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
    @OneToOne
    @JoinColumn(name = "photo_id")
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
                ", photo=" + photo +
                '}';
    }

    // Getters/setters
}
