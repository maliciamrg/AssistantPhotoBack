package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.repository.TagNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    private TagService tagService;

    @BeforeEach
    void setUp() throws Exception {
        // Mock dependencies
        TagNodeRepository mockRepo = mock(TagNodeRepository.class);
        MyConfig mockConfig = mock(MyConfig.class);

        // Instantiate service
        tagService = new TagService(mockRepo, mockConfig);
    }

    @Test
    void testTrimAndLowercase() {
        assertEquals("hello", tagService.normalizeTagName("  HELLO "));
        assertEquals("world", tagService.normalizeTagName(" World "));
    }

    @Test
    void testAccentsAreRemoved() {
        assertEquals("eleve", tagService.normalizeTagName("Élève"));
        assertEquals("francois", tagService.normalizeTagName("François"));
        assertEquals("cafe", tagService.normalizeTagName("café"));
    }

    @Test
    void testSpacesAndDashesReplacedWithUnderscores() {
        assertEquals("some_tag_name", tagService.normalizeTagName("Some-Tag Name"));
        assertEquals("fete_nationale", tagService.normalizeTagName("Fête-Nationale"));
    }

    @Test
    void testMixedChanges() {
        assertEquals("ecole_nationale", tagService.normalizeTagName(" École Nationale "));
        assertEquals("eleve_bon", tagService.normalizeTagName(" Élève Bon "));
    }

    @Test
    void testMixedChanges2() {
        assertEquals("m31_galaxie_andromede", tagService.normalizeTagName("m31-galaxie-andromede"));
    }

    @Test
    void testNullInput() {
        assertNull(tagService.normalizeTagName(null));
    }

    @Test
    void testEmptyString() {
        assertEquals("", tagService.normalizeTagName(""));
        assertEquals("", tagService.normalizeTagName("   "));
    }
}
