package com.malicia.mrg.assistant.photo.file;

import com.malicia.mrg.assistant.photo.repertoire.Photo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

class WorkWithFileTest {

    @Test
    void testConvertPathsToPhotos_ok_with_xmp_full() throws IOException {
        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-06-29_spectacle_antony_laureline";
        String photo1 = "49.0xiaomi 2201117ty_camera_2023-10-27_14-54-49_img_20231027_145449.jpg";
        List<Photo> result = WorkWithFile.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(true, result.get(0).getFlagged());
        Assertions.assertEquals("reject", result.get(0).getFlagType());
        Assertions.assertEquals(0, result.get(0).getStarred());
        Assertions.assertEquals("1992-12-01T00:00:00", result.get(0).getCreatedDate());
        Assertions.assertEquals(5, result.get(0).getTags().size());
        Assertions.assertEquals("Salon", result.get(0).getTags().get(1));
        Assertions.assertEquals("Bateau à voile", result.get(0).getTags().get(3));
    }
    @Test

    void testConvertPathsToPhotos_ok_with_xmp_not_full() throws IOException {
        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-06-29_spectacle_antony_laureline";
        String photo1 = "58.0xiaomi 2201117ty_camera_2023-10-27_17-20-48_img_20231027_172048.jpg";
        List<Photo> result = WorkWithFile.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(true, result.get(0).getFlagged());
        Assertions.assertEquals("pick", result.get(0).getFlagType());
        Assertions.assertEquals(3, result.get(0).getStarred());
        Assertions.assertEquals("2025-01-31 22:08:37", result.get(0).getCreatedDate());
        Assertions.assertEquals(6, result.get(0).getTags().size());
        Assertions.assertEquals("Salon", result.get(0).getTags().get(1));
        Assertions.assertEquals("Bateau à voile", result.get(0).getTags().get(3));
    }

    @Test
    void testConvertPathsToPhotos_ok_without_xmp() throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        //redirect sysout
        System.setOut(new PrintStream(outContent));

        String pathToScan = "./src/test/resources/50-Phototheque/##Events 10-15 j/2023-06-29_spectacle_antony_laureline";
        String photo1 = "file_example_MP4_480_1_5MG.mp4";
        List<Photo> result = WorkWithFile.convertPathsToPhotos(pathToScan, List.of(Path.of(pathToScan+"/"+photo1)));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(true, result.get(0).getFlagged());
        Assertions.assertEquals("pick", result.get(0).getFlagType());
        Assertions.assertEquals(0, result.get(0).getStarred());
        Assertions.assertEquals("2024-11-24 22:01:54", result.get(0).getCreatedDate());
        Assertions.assertEquals(0, result.get(0).getTags().size());


        String output = outContent.toString();
        //sysout re out to original output
        System.setOut(originalOut);
        //sysout(in original) captured sysout
        System.out.println(output);

        Assertions.assertTrue(output.contains("XMP sidecar file does not exist."));

    }

}
