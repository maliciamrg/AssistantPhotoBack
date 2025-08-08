package com.malicia.mrg.assistant.photo.repertoire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootTypeEnum;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
class RootRepertoireTest {
    private static final Logger logger = LoggerFactory.getLogger(RootRepertoireTest.class);

    @Autowired
    private MyConfig mockConfig; // Mocking the MyConfig dependency

    @Autowired
    private PhotoshootService photoshootService; // Mocking the MyConfig dependency

    @SpyBean
    private PhotoService photoService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ThumbnailService thumbnailService;

    @MockBean
    private PhotoRepository photoRepository;

    @BeforeEach
    void setUp() {
        //no photo stock in database
        when(photoRepository.findByHash(anyString()))
                .thenReturn(Optional.empty());
        //clearRedisPhotoCache()
        redisConnectionFactory.getConnection().flushDb();
    }

    // recuperer uniquement les Repertoires AllIn (photo non rafiné a tirer /grouper)
    @Test
    void getAllPhotoshootFromPhotoshootTypeAllIn() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());

        //then
        logger.debug(allPhotoshoot.toString());
        for (Photoshoot photoshoot : allPhotoshoot) {
            logger.debug(String.valueOf(photoshoot));
        }
        assertEquals(2, allPhotoshoot.size());
    }

    // recuperer uniquement les photo repertoire EVENTS
    @Test
    void getAllPhotoshootFromPhotoshootTypeEvents() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot().get(0));

        //then
        logger.debug(allPhotoshoot.toString());
        for (Photoshoot Photoshoot : allPhotoshoot) {
            logger.debug(Photoshoot.toString());
        }
        assertEquals(2, allPhotoshoot.size());
    }

    // recuperer uniquement les photo repertoire AllIn du Photoshoot
    @Test
    void getAllPhotoRepertoireAllInFromSeanceRepertoire() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);

        //when
        List<Photoshoot> photoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());

        //then
        logger.debug(photoshoot.toString());
        for (Photoshoot Photoshoot : photoshoot) {
            logger.debug(Photoshoot.toString());
        }
        assertEquals(2, photoshoot.size());
    }

    // recuperer uniquement le Repertoires de travail de assitant
    @Test
    void getAllPhotoshootFromPhotoshootTypeASSISTANT_WORK() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);

        //when
        List<Photoshoot> allPhotoshoot = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ASSISTANT_WORK.name()).getPhotoshootRoot());

        //then
        logger.debug(allPhotoshoot.toString());
        for (Photoshoot photoshoot : allPhotoshoot) {
            logger.debug(String.valueOf(photoshoot));
        }
        assertEquals(1, allPhotoshoot.size());
        assertEquals("src\\test\\resources\\10-Assistant_work\\2023_04_08_(00026)", allPhotoshoot.get(0).getPath());
    }

//    // recupere un list de photo depuis un repertoire
//    @Test
//    void getAllPhotoFromPhotoshootTypeAllIn() {
//        //given
//        Path rootTest = Paths.get("src", "test", "resources");
//        mockConfig.setRootPath("./" + rootTest + "/");
//        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);
//
//        //when
//        List<Photoshoot> rootRepPhotoshootList = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot());
//        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromListPhotoshoot(rootRepPhotoshootList);
//
//        //then
//        logger.debug(String.valueOf(allPhotoFromSeanceRepertoire));
//        for (PhotoDTO photo : allPhotoFromSeanceRepertoire) {
//            logger.debug(String.valueOf(photo));
//        }
//        assertEquals(7, allPhotoFromSeanceRepertoire.size());
//    }

    // recupere un list de photo depuis un repertoire
    @Test
    void getAllPhotoFromPhotoshootTypeEvents_byPhotoshoot() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);

        //when
        List<Photoshoot> assistantRepertoire = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot().get(0));
        PhotoGroup allPhotoFromPhotoRepertoire0 = photoshootService.getAllPhotoFromPhotoshoot(assistantRepertoire.get(0));
        PhotoGroup allPhotoFromPhotoRepertoire1 = photoshootService.getAllPhotoFromPhotoshoot(assistantRepertoire.get(1));

        //then
        logger.debug(String.valueOf(allPhotoFromPhotoRepertoire0));
        for (PhotoDTO photo : allPhotoFromPhotoRepertoire0) {
            logger.debug(String.valueOf(photo));
        }
        assertEquals(3, allPhotoFromPhotoRepertoire0.size());

        logger.debug(String.valueOf(allPhotoFromPhotoRepertoire1));
        for (PhotoDTO photo : allPhotoFromPhotoRepertoire1) {
            logger.debug(String.valueOf(photo));
        }
        assertEquals(1, allPhotoFromPhotoRepertoire1.size());
    }


//    // recupere un list de photo depuis le repertoire reel
//    @Disabled("Not a real test, use to create Json getAllPhotoFromAllInRealToJsonTEST from real data ")
//    @Test
//    void getAllPhotoFromPhotoshootTypeAllInToJson() {
//        //given
//        mockConfig.setRootPath("\\\\192.212.40.111\\80-Photo\\");
//        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);
//        String jsonDest = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST-out.json";
//        //reduce to the 10 first photo
//        doAnswer(invocation -> {
//            String pathToScan = invocation.getArgument(0);
//            List<Path> originalList = invocation.getArgument(1);
//            List<Path> trimmed = originalList.stream().limit(10).toList();
//            return photoService.convertPathsToPhotos(pathToScan, trimmed);
//        }).when(photoService).convertPathsToPhotos(anyString(), argThat(list -> list != null && list.size() > 20));
//
//        //when
//        List<Photoshoot> assistantRepertoire = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.ALL_IN.name()).getPhotoshootRoot().get(0));
//        List<Photoshoot> assistantRepertoireFiltered = assistantRepertoire.stream()
//                .filter(p -> "AllIn".equals(p.getName()))
//                .collect(Collectors.toList());
//        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoireToJson(assistantRepertoireFiltered, jsonDest);
//
//        //then
//        ObjectMapper objectMapper = new ObjectMapper();
//        File file = new File(jsonDest);
//        PhotoGroup allPhotoFromSeanceRepertoireFromFile = new PhotoGroup();
//        try {
//            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<PhotoGroup>() {
//            });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        logger.debug(" --> " + allPhotoFromSeanceRepertoireFromFile.getPhotos().size() + " == " + allPhotoFromSeanceRepertoire.size() + " <-- ");
//        assertEquals(allPhotoFromSeanceRepertoireFromFile.getPhotos().size(), allPhotoFromSeanceRepertoire.size());
//    }

//    // recupere un list de photo depuis un repertoire
//    //@Disabled("doesnt work on jenkins")
//    @Test
//    void getAllPhotoFromPhotoshootTypeEvents() {
//        //given
//        Path rootTest = Paths.get("src", "test", "resources");
//        mockConfig.setRootPath("./" + rootTest + "/");
//        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);
//
//        //when
//        List<Photoshoot> photoshootListEvents = rootRep.getPhotoshootList(photoshootService.getPhotoshootType(PhotoshootTypeEnum.EVENTS.name()).getPhotoshootRoot());
//        PhotoGroup allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromListPhotoshoot(photoshootListEvents);
//
//        //then
//        logger.debug(photoshootListEvents.toString());
//        logger.debug(String.valueOf(allPhotoFromSeanceRepertoire));
//        for (PhotoDTO photo : allPhotoFromSeanceRepertoire) {
//            logger.debug(String.valueOf(photo));
//        }
//        assertEquals(4, allPhotoFromSeanceRepertoire.size());
//    }

    // Group photo
    @Disabled("not usefull anymore")
    @Test
    void getGroupOfPhoto_FromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);
        String jsonSrc = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST.json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonSrc);
        PhotoGroup allPhotoFromSeanceRepertoireFromFile = new PhotoGroup();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<PhotoGroup>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when

        List<PhotoGroup> repPhotoGroupFrom = new ArrayList<>();
        repPhotoGroupFrom.add(allPhotoFromSeanceRepertoireFromFile);

        //then
        logger.debug(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + repPhotoGroupFrom.size() + " <-- ");
        assertEquals(145, repPhotoGroupFrom.size());
        assertEquals(5, repPhotoGroupFrom.get(0).size());
        assertEquals(8, repPhotoGroupFrom.get(1).size());
        assertEquals(26, repPhotoGroupFrom.get(2).size());
        logger.debug(repPhotoGroupFrom.get(2).toString());
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
        RootRepertoire rootRep = new RootRepertoire(mockConfig, photoService);
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
        int ret = RootRepertoire.moveGroupToDestinationFolder(mockConfig.getRootPath() + allPhotoshoot.get(0).getPath(), photoGroupFrom, true, true);

        //then
        logger.debug(allPhotoshoot.toString());
        assertEquals(26, photoGroupFrom.size());
        assertEquals(26, ret);
    }
}

