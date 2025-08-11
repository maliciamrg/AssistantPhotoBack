package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.dto.TagNodeDto;
import com.malicia.mrg.assistant.photo.entity.TagNode;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTags() throws Exception {
        // Arrange
        List<TagNode> tags = Arrays.asList(new TagNode(), new TagNode());
        when(tagService.getRootTags()).thenReturn(tags);

        // Act
        List<TagNodeDto> result = tagController.getTags();

        // Assert
        assertEquals(tags, result);
    }

    @Test
    public void testGetTagById_Found() {
        // Arrange
        Long id = 1L;
        TagNode node = new TagNode();
        when(tagService.getTagById(id)).thenReturn(node);

        // Act
        TagNodeDto result = tagController.getTagById(id);

        // Assert
        assertEquals(node, result);
    }

    @Test
    public void testGetTagById_NotFound() {
        // Arrange
        Long id = 1L;
        when(tagService.getTagById(id)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> tagController.getTagById(id));
    }

    @Test
    public void testGetTagByName_Found() {
        // Arrange
        String name = "tagName";
        TagNode node = new TagNode();
        when(tagService.getTagByName(name)).thenReturn(node);

        // Act
        TagNodeDto result = tagController.getTagByName(name);

        // Assert
        assertEquals(node, result);
    }

    @Test
    public void testGetTagByName_NotFound() {
        // Arrange
        String name = "tagName";
        when(tagService.getTagByName(name)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> tagController.getTagByName(name));
    }
}