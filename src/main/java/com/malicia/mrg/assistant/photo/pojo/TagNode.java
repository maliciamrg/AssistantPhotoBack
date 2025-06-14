package com.malicia.mrg.assistant.photo.pojo;

import java.util.List;

public class TagNode {
    private Long id;
    private String name;
    private Double dateCreated;
    private List<TagNode> children;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getDateCreated() { return dateCreated; }
    public void setDateCreated(Double dateCreated) { this.dateCreated = dateCreated; }

    public List<TagNode> getChildren() { return children; }
    public void setChildren(List<TagNode> children) { this.children = children; }
}
