package com.malicia.mrg.assistant.photo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.parameter.GroupPhoto;
import com.malicia.mrg.assistant.photo.parameter.RepertoireOfType;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.pojo.TagNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
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
    private List<SeanceType> seanceType;
    private List<RepertoireOfType> repertoireOfType;

    public MyConfig() {
        try {
            setTagsList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TagNode> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<TagNode> tagsList) {
        this.tagsList = tagsList;
    }

    public void setTagsList() throws Exception {
        this.tagsList = loadTagsFromJson();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SeanceType> getSeanceType() {
        return seanceType;
    }

    public void setSeanceType(List<SeanceType> seanceType) {
        this.seanceType = seanceType;
    }

    public List<RepertoireOfType> getRepertoireOfType() {
        return repertoireOfType;
    }

    public void setRepertoireOfType(List<RepertoireOfType> repertoireOfType) {
        this.repertoireOfType = repertoireOfType;
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

    private List<TagNode> loadTagsFromJson() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("lightroom_tags_grouped.json");
        List<TagNode> tags = new ObjectMapper().readValue(is, new TypeReference<List<TagNode>>() {});
        return tags;
    }
}
