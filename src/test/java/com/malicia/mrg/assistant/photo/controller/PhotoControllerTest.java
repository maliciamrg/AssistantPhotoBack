package com.malicia.mrg.assistant.photo.controller;

import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.XMPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoControllerTest {

    @Mock
    private RootRepertoire rootRep;

    @Mock
    private PhotoService photoService;

    @Mock
    private XMPService xmpService;

    @InjectMocks
    private PhotoController photoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void batchUpdate_shouldReturnPhotoCount_whenPhotosAreValid() {
        // Arrange
        List<Photo> photos = List.of(new Photo(), new Photo());
        //doNothing().when(photoService).saveAllPhotos(photos, true);
        when(photoService.saveAllPhotos(anyList(), eq(true))).thenReturn(photos);

        // Act
        ResponseEntity<Map<String, String>> response = photoController.batchUpdate(photos);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("2", response.getBody().get("photoCount"));

        verify(photoService, times(1)).saveAllPhotos(photos, true);
    }

    @Test
    void batchUpdate_shouldReturnBadRequest_whenIllegalArgumentThrown() {
        // Arrange
        List<Photo> photos = List.of(new Photo());
        doThrow(new IllegalArgumentException("Invalid")).when(photoService).saveAllPhotos(photos, true);

        // Act
        ResponseEntity<Map<String, String>> response = photoController.batchUpdate(photos);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(photoService, times(1)).saveAllPhotos(photos, true);
    }
}
