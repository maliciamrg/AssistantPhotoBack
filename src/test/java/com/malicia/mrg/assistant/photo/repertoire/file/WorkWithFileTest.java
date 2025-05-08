package com.malicia.mrg.assistant.photo.repertoire.file;

import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WorkWithFileTest {

    @MockBean
    private PhotoService photoService;

    @Test
    void getRightDateFromPhoto() {
        //given
        Path rootFileTest = Paths.get("src", "test", "resources","00-CheckIn","20240212-_DSC4316.ARW");
        List<Path> rootFilesTest = new ArrayList<>();
        rootFilesTest.add(rootFileTest);
        List<Photo> expectedList = new ArrayList<>();

        //when
        try {
            expectedList = WorkWithFile.convertPathsToPhotos("\\src\\test\\resources\\00-CheckIn\\", rootFilesTest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //then
        System.out.println(expectedList);
        assertEquals(1, expectedList.size());
        assertEquals("2024-02-12 10:49:42", expectedList.get(0).getExifDate());
    }
}