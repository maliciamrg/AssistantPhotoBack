package com.malicia.mrg.assistant.photo.pojo;

import java.io.Serializable;

public class Photoshoot implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String path;  // Path or description of the repertoire
    private String description; // Optional description for the repertoire
    private PhotoGroup photoGroup;
    private PhotoshootMetaData metaDataFromPhotoshoot;

    // Constructor
    public Photoshoot(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public Photoshoot() {
    }

    @Override
    public String toString() {
        return "Photoshoot{" + "name='" + name + '\'' + ", path='" + path + '\'' + ", description='" + description + '\'' + '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhotoGroup getGroupOfPhoto() {
        return photoGroup;
    }

    public void setGroupOfPhoto(PhotoGroup photoGroup) {
        this.photoGroup = photoGroup;
    }

    public PhotoshootMetaData getMetaDataFromPhotoshoot() {
        return metaDataFromPhotoshoot;
    }

    public void setMetaDataFromPhotoshoot(PhotoshootMetaData metaDataFromPhotoshoot) {
        this.metaDataFromPhotoshoot = metaDataFromPhotoshoot;
    }
}