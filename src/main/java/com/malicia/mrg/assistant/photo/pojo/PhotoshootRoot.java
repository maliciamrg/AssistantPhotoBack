package com.malicia.mrg.assistant.photo.pojo;

import java.io.Serializable;

public class PhotoshootRoot implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String path;  // Path or description of the repertoire
    private String description; // Optional description for the repertoire

    // Constructor
    public PhotoshootRoot(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public PhotoshootRoot() {
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}