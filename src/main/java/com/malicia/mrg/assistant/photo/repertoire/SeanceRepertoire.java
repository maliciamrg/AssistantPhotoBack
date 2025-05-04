package com.malicia.mrg.assistant.photo.repertoire;

import java.io.Serializable;

public class SeanceRepertoire implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String path;  // Path or description of the repertoire
    private String description; // Optional description for the repertoire

    // Constructor
    public SeanceRepertoire(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public SeanceRepertoire() {
    }

    @Override
    public String toString() {
        return "SeanceRepertoire{" + "id='" + id + '\'' + ", path='" + path + '\'' + ", description='" + description + '\'' + '}';
    }

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}