package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.dto.TagNodeDto;
import com.malicia.mrg.assistant.photo.entity.TagNode;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // CREATE a new tag
    @PostMapping
    public ResponseEntity<TagNodeDto> createTag(@RequestBody TagNodeDto tagDto) {
        tagDto.setId(tagService.getNextFreeTagId());
        TagNode created = tagService.createTag(tagDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TagNodeDto(created));
    }

    // UPDATE a tag by ID
    @PutMapping("/{id}/rename")
    public ResponseEntity<Void> updateTag(@PathVariable Long id, @RequestBody String name) {
        int ret = tagService.updateTagName(id, name);
        return (ret !=0 ? ResponseEntity.ok().build() :ResponseEntity.badRequest().build());
    }

    // UPDATE a tag by ID
    @PutMapping("/{id}")
    public ResponseEntity<TagNodeDto> updateTag(@PathVariable Long id, @RequestBody TagNodeDto tagDto) {
        TagNode updated = tagService.updateTag(id, tagDto);
        return ResponseEntity.ok(new TagNodeDto(updated));
    }

    // DELETE a tag by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public List<TagNodeDto> getTags() throws Exception {
        List<TagNodeDto> rootTagsDto = new ArrayList<>();
        for (TagNode rootTag : tagService.getRootTags()) {
            rootTagsDto.add(new TagNodeDto(rootTag));
        }
        return rootTagsDto;
    }

    @GetMapping("/{id}")
    public TagNodeDto getTagById(@PathVariable Long id) {
        TagNode node = tagService.getTagById(id);
        if (node == null) {
            throw new NotFoundException("Tag with ID " + id + " not found");
        }
        return new TagNodeDto(node);
    }

    @GetMapping("/by-name/{name}")
    public TagNodeDto getTagByName(@PathVariable String name) {
        TagNode node = tagService.getTagByName(name);
        if (node == null) {
            throw new NotFoundException("Tag with name '" + name + "' not found");
        }
        return new TagNodeDto(node);
    }

}
