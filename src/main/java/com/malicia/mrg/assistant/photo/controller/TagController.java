package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.exception.TagNotFoundException;
import com.malicia.mrg.assistant.photo.pojo.TagNode;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping()
    public List<TagNode> getTags() throws Exception {
        return tagService.getRootTags();
    }

    @GetMapping("/{id}")
    public TagNode getTagById(@PathVariable Long id) {
        TagNode node = tagService.getTagById(id);
        if (node == null) {
            throw new TagNotFoundException("Tag with ID " + id + " not found");
        }
        return node;
    }

    @GetMapping("/by-name/{name}")
    public TagNode getTagByName(@PathVariable String name) {
        TagNode node = tagService.getTagByName(name);
        if (node == null) {
            throw new TagNotFoundException("Tag with name '" + name + "' not found");
        }
        return node;
    }

}
