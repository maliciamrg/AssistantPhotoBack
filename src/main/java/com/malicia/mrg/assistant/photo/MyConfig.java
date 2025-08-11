package com.malicia.mrg.assistant.photo;

import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "assistant")
public class MyConfig {
    private boolean dryRun;
    private String name;
    private String rootPath;
    private String tagFileName;
    private List<String> fileExtensionsToWorkWith;
    private GroupPhoto groupPhoto;
    private List<PhotoshootType> photoshootType;
    public MyConfig() {
    }

    public String getTagFileName() {
        return tagFileName;
    }

    public void setTagFileName(String tagFileName) {
        this.tagFileName = tagFileName;
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
