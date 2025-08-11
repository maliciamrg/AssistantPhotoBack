package com.malicia.mrg.assistant.photo.dto;

import com.malicia.mrg.assistant.photo.entity.TagNode;

public class TagNodeDto {
    private Long id;
    private String name;
    private Long parentId;

    public TagNodeDto(TagNode tagNode) {
        this.id = tagNode.getId();
        this.name = tagNode.getName();
        this.parentId = tagNode.getParent()!=null? tagNode.getParent().getId():null;
    }

    public TagNodeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    // constructor, getters
}
