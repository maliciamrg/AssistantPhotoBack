package com.malicia.mrg.assistant.photo.file;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class FileSystemServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemServiceTest.class);

    @Autowired
    private PhotoService photoService;
    @MockBean
    PhotoRepository photoRepository;

    @BeforeEach
    public void setUp() {

    }

//    @Test
//    void testConvertPathsToPhotos_ok_with_xmp_full() throws IOException {
//        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-10-27_spectacle_antony_laureline";
//        String photo1 = "49.0xiaomi 2201117ty_camera_2023-10-27_14-54-49_img_20231027_145449.jpg";
//        List<PhotoDTO> result = photoService.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
//        Assertions.assertEquals(1, result.size());
//        Assertions.assertEquals(1, result.get(0).getRating());
//        Assertions.assertEquals(null, result.get(0).getLabel());
//        Assertions.assertEquals(0, result.get(0).getPick());
//        Assertions.assertEquals("1992-12-01T00:00:00", result.get(0).getCreateDate());
//        Assertions.assertEquals(5, result.get(0).getKeywords().size());
//        Assertions.assertEquals("Salon", result.get(0).getKeywords().get(1));
//        Assertions.assertEquals("Bateau à voile", result.get(0).getKeywords().get(3));
//    }
//    @Test
//
//    void testConvertPathsToPhotos_ok_with_xmp_not_full() throws IOException {
//        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-10-27_spectacle_antony_laureline";
//        String photo1 = "58.0xiaomi 2201117ty_camera_2023-10-27_17-20-48_img_20231027_172048.jpg";
//        List<PhotoDTO> result = photoService.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
//        Assertions.assertEquals(1, result.size());
//        Assertions.assertEquals(2, result.get(0).getRating());
//        Assertions.assertEquals(null, result.get(0).getLabel());
//        Assertions.assertEquals(0, result.get(0).getPick());
//        Assertions.assertEquals("2023-10-27 17:20:48", result.get(0).getExifDate());
//        Assertions.assertEquals(6, result.get(0).getKeywords().size());
//        Assertions.assertEquals("Salon", result.get(0).getKeywords().get(1));
//        Assertions.assertEquals("Bateau à voile", result.get(0).getKeywords().get(3));
//    }

//    @Test
//    void testConvertPathsToPhotos_ok_without_xmp() {
//        // Mock repository to simulate photo not found
//        Mockito.when(photoRepository.findByHash(Mockito.anyString()))
//                .thenReturn(Optional.empty());
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        PrintStream originalOut = System.out;
//        //redirect sysout
//        System.setOut(new PrintStream(outContent));
//
//        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-10-27_spectacle_antony_laureline";
//        String photo1 = "file_example_MP4_480_1_5MG.mp4";
//        List<PhotoDTO> result = photoService.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
//        Assertions.assertEquals(1, result.size());
//        Assertions.assertEquals(0, result.get(0).getRating());
//        Assertions.assertEquals(null, result.get(0).getLabel());
//        Assertions.assertEquals(0, result.get(0).getPick());
//        Assertions.assertEquals("2024-11-24 22:01:54", result.get(0).getCreatedDate());
//        Assertions.assertEquals(0, result.get(0).getKeywords().size());
//
//
//        String output = outContent.toString();
//        //sysout re out to original output
//        System.setOut(originalOut);
//        //sysout(in original) captured sysout
//        logger.debug(output);
//
//        Assertions.assertTrue(output.contains("XMP sidecar file does not exist."));
//
//    }

}
