package com.malicia.mrg.assistant.photo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.exception.CustomException;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.pojo.TagNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "assistant")
public class MyConfig {

    public List<TagNode> tagsList;
    private boolean dryRun;
    private String name;
    private String rootPath;
    private List<String> fileExtensionsToWorkWith;
    private GroupPhoto groupPhoto;
    private List<PhotoshootType> photoshootType;

    public MyConfig() {
        try {
            setTagsList();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public List<TagNode> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<TagNode> tagsList) {
        this.tagsList = tagsList;
    }

    public void setTagsList() {
        this.tagsList = loadTagsFromJson();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PhotoshootType> getPhotoshootType() {
        return photoshootType;
    }

    public void setPhotoshootType(List<PhotoshootType> photoshootType) {
        this.photoshootType = photoshootType;
    }


    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public List<String> getFileExtensionsToWorkWith() {
        return fileExtensionsToWorkWith;
    }

    public void setFileExtensionsToWorkWith(List<String> fileExtensionsToWorkWith) {
        this.fileExtensionsToWorkWith = fileExtensionsToWorkWith;
    }

    public GroupPhoto getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(GroupPhoto groupPhoto) {
        this.groupPhoto = groupPhoto;
    }

    public boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    private List<TagNode> loadTagsFromJson() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("lightroom_tags_grouped.json");
        try {
            return new ObjectMapper().readValue(is, new TypeReference<List<TagNode>>() {
            });
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

    public static class GroupPhoto {
        private int ecartEnMinutes;
        private int photoMin;

        public int getPhotoMin() {
            return photoMin;
        }

        public void setPhotoMin(int photoMin) {
            this.photoMin = photoMin;
        }

        public int getEcartEnMinutes() {
            return ecartEnMinutes;
        }

        public void setEcartEnMinutes(int ecartEnMinutes) {
            this.ecartEnMinutes = ecartEnMinutes;
        }
    }
}
