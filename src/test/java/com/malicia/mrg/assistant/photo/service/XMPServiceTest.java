package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.pojo.XMPPhoto;
import com.malicia.mrg.assistant.photo.MyConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@SpringBootTest
class XMPServiceTest {
    @MockBean
    private CacheService redisTemplate;
    @Autowired
    private MyConfig mockConfig; // Mocking the MyConfig dependency

    @Test
    void testReadMetadata() throws IOException, XMPException {

        XMPPhoto result = XMPService.readMetadata(mockConfig.getRootPath() + "20250522-_DSC5845.xmp");
        Assertions.assertEquals(-1, result.getPick());
        Assertions.assertEquals("Blue", result.getLabel());
        Assertions.assertEquals(4, result.getKeywords().length);
        Assertions.assertEquals("sea", result.getKeywords()[2]);
        Assertions.assertEquals("2025-05-22T12:59:17", result.getCreateDate());
        Assertions.assertEquals(3, result.getRating());
    }

    @Test
    void testStoreMetadata() throws IOException, XMPException {
        // 1. Définir le chemin du fichier source et du fichier de test
        String sourceXmpPath = mockConfig.getRootPath() + "20250522-_DSC5845.xmp";
        String testXmpPath = mockConfig.getRootPath() + "20250522-_DSC5845_testStoreMetadata.xmp";

        // 2. Copier le fichier source vers un fichier temporaire
        Files.copy(new File(sourceXmpPath).toPath(), new File(testXmpPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 3. Créer un DTO avec des valeurs modifiées
        XMPPhoto dto = new XMPPhoto();
        dto.setRating(5); // Changer la note
        dto.setLabel("Green"); // Nouveau label
        dto.setCreateDate("2025-06-01T18:30:00"); // Date différente
        dto.setPick(1); // Nouveau flag
        dto.setKeywords(new String[]{"nature", "wildlife", "sunset"}); // Remplace les mots-clés

        // 4. Appeler storeMetadata pour modifier les métadonnées du fichier de test
        XMPService.storeMetadata(dto, testXmpPath);

        // 5. Lire à nouveau pour vérifier les valeurs modifiées
        XMPPhoto result = XMPService.readMetadata(testXmpPath);

        Assertions.assertEquals(5, result.getRating());
        Assertions.assertEquals("Green", result.getLabel());
        Assertions.assertEquals("2025-06-01T18:30:00", result.getCreateDate());
        Assertions.assertEquals(1, result.getPick());
        Assertions.assertArrayEquals(new String[]{"nature", "wildlife", "sunset"}, result.getKeywords());

    }
}
