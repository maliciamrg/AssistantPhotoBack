package com.malicia.mrg.assistant.photo.repertoire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.service.*;
import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootTypeEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RootRepertoireTest {

    @Autowired
    private MyConfig mockConfig; // Mocking the MyConfig dependency

    @Autowired
    private PhotoshootService photoshootService; // Mocking the MyConfig dependency

    @Autowired
    private PhotoService photoService;

    @MockBean
    private ThumbnailService thumbnailService;

    // recuperer uniquement les Repertoires AllIn (photo non rafiné a tirer /grouper)
    @Test
    void getAllPhotoshootFromPhotoshootTypeAllIn() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());

        //then
        System.out.println(allPhotoshoot);
        for (Photoshoot photoshoot : allPhotoshoot) {
            System.out.println(photoshoot);
        }
        assertEquals(2, allPhotoshoot.size());
    }

    // recuperer uniquement les photo repertoire EVENTS
    @Test
    void getAllPhotoshootFromPhotoshootTypeEvents() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot().get(0));

        //then
        System.out.println(allPhotoshoot);
        for (Photoshoot Photoshoot : allPhotoshoot) {
            System.out.println(Photoshoot.toString());
        }
        assertEquals(2, allPhotoshoot.size());
    }

    // recuperer uniquement les photo repertoire AllIn du Photoshoot
    @Test
    void getAllPhotoRepertoireAllInFromSeanceRepertoire() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> photoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());

        //then
        System.out.println(photoshoot);
        for (Photoshoot Photoshoot : photoshoot) {
            System.out.println(Photoshoot.toString());
        }
        assertEquals(2, photoshoot.size());
    }

    // recuperer uniquement le Repertoires de travail de assitant
    @Test
    void getAllPhotoshootFromPhotoshootTypeASSISTANT_WORK() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ASSISTANT_WORK.name()).getPhotoshootRoot());

        //then
        System.out.println(allPhotoshoot);
        for (Photoshoot photoshoot : allPhotoshoot) {
            System.out.println(photoshoot);
        }
        assertEquals(1, allPhotoshoot.size());
        assertEquals(".\\src\\test\\resources\\10-Assistant_work\\2023_04_08_(00026)", allPhotoshoot.get(0).getPath());
    }

    // recupere un list de photo depuis un repertoire
    @Test
    void getAllPhotoFromPhotoshootTypeAllIn() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> rootRepPhotoshootList = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());
        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromListPhotoshoot(rootRepPhotoshootList);

        //then
        System.out.println(allPhotoFromSeanceRepertoire);
        for (PhotoDTO photo : allPhotoFromSeanceRepertoire) {
            System.out.println(photo);
        }
        assertEquals(7, allPhotoFromSeanceRepertoire.size());
    }

    // recupere un list de photo depuis un repertoire
    @Test
    void getAllPhotoFromPhotoshootTypeEvents_byPhotoshoot() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> assistantRepertoire = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot().get(0));
        PhotoGroup allPhotoFromPhotoRepertoire0 = rootRep.getAllPhotoFromPhotoshoot(assistantRepertoire.get(0));
        PhotoGroup allPhotoFromPhotoRepertoire1 = rootRep.getAllPhotoFromPhotoshoot(assistantRepertoire.get(1));

        //then
        System.out.println(allPhotoFromPhotoRepertoire0);
        for (PhotoDTO photo : allPhotoFromPhotoRepertoire0) {
            System.out.println(photo);
        }
        assertEquals(3, allPhotoFromPhotoRepertoire0.size());

        System.out.println(allPhotoFromPhotoRepertoire1);
        for (PhotoDTO photo : allPhotoFromPhotoRepertoire1) {
            System.out.println(photo);
        }
        assertEquals(1, allPhotoFromPhotoRepertoire1.size());
    }



    // recupere un list de photo depuis le repertoire reel
    @Disabled("Not a real test, use to create Json getAllPhotoFromAllInRealToJsonTEST from real data ")
    @Test
    void getAllPhotoFromPhotoshootTypeAllInToJson() {
        //given
        mockConfig.setRootPath("\\\\192.212.5.111\\80-Photo\\");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);
        String jsonDest = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST-out.json";

        //when
        List<Photoshoot> assistantRepertoire = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot().get(0));
        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoireToJson(assistantRepertoire, jsonDest);

        //then
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonDest);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + allPhotoFromSeanceRepertoire.size() + " <-- ");
        assertEquals(allPhotoFromSeanceRepertoireFromFile.size(), allPhotoFromSeanceRepertoire.size());
    }

    // recupere un list de photo depuis un repertoire
    //@Disabled("doesnt work on jenkins")
    @Test
    void getAllPhotoFromPhotoshootTypeEvents() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);

        //when
        List<Photoshoot> photoshootListEvents = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot());
        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromListPhotoshoot(photoshootListEvents);

        //then
        System.out.println(photoshootListEvents);
        System.out.println(allPhotoFromSeanceRepertoire);
        for (PhotoDTO photo : allPhotoFromSeanceRepertoire) {
            System.out.println(photo);
        }
        assertEquals(4, allPhotoFromSeanceRepertoire.size());
    }

    // Group photo
    @Test
    void getGroupOfPhoto_FromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);
        String jsonSrc = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST.json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonSrc);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        List<PhotoGroup> repPhotoGroupFrom = rootRep.getGroupOfPhotoFrom(allPhotoFromSeanceRepertoireFromFile);

        //then
        System.out.println(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + repPhotoGroupFrom.size() + " <-- ");
        assertEquals(145, repPhotoGroupFrom.size());
        assertEquals(5, repPhotoGroupFrom.get(0).size());
        assertEquals(8, repPhotoGroupFrom.get(1).size());
        assertEquals(26, repPhotoGroupFrom.get(2).size());
        System.out.println(repPhotoGroupFrom.get(2).toString());
        assertEquals(13, repPhotoGroupFrom.get(3).size());
        assertEquals(12, repPhotoGroupFrom.get(4).size());
        assertEquals(6161, repPhotoGroupFrom.get(repPhotoGroupFrom.size() - 1).size());

        //given
        String jsonDest = mockConfig.getRootPath() + "/getGroupOfPhotoTEST-out.json";
        FileSystemService.putIntoJsonFile(repPhotoGroupFrom.get(2), jsonDest);

    }

    // Move Group photo
    //@Disabled("doesnt work on jenkins")
    @Test
    void moveGroupToDestinationFolder_FromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig,photoService);
        String jsonSrc = mockConfig.getRootPath() + "/getGroupOfPhotoTEST.json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonSrc);
        PhotoGroup photoGroupFrom = new PhotoGroup();
        try {
            photoGroupFrom = objectMapper.readValue(file, new TypeReference<PhotoGroup>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ASSISTANT_WORK.name()).getPhotoshootRoot().get(0));
        int ret = RootRepertoire.moveGroupToDestinationFolder(mockConfig.getRootPath() + allPhotoshoot.get(0).getPath(), photoGroupFrom, true ,true);

        //then
        System.out.println(allPhotoshoot);
        assertEquals(26, photoGroupFrom.size());
        assertEquals(26, ret);
    }
}

