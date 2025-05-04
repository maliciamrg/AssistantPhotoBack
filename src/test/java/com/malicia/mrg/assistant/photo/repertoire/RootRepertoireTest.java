package com.malicia.mrg.assistant.photo.repertoire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
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
    
    private RootRepertoire rootRepertoire; // Injecting the mock into the RootRepertoire constructor

    @MockBean
    private EntityManagerFactory entityManagerFactory;
    @MockBean
    private EntityManager entityManager;
    @MockBean
    private PhotoService photoService;
    @MockBean
    private DataSource dataSource;
    @MockBean
    private PhotoRepository photoRepository;

    @BeforeEach
    void setUp() {

    }

    // recuperer uniquement les Repertoires AllIn (photo non rafiné a tirer /grouper)
    @Test
    void getAllSeanceRepertoireAllin() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);

        //then
        System.out.println(allSeanceRepertoire);
        for (SeanceRepertoire seanceRepertoire : allSeanceRepertoire) {
            System.out.println(seanceRepertoire);
        }
        assertEquals(2, allSeanceRepertoire.size());
    }

    // recuperer uniquement les photo repertoire EVENTS
    @Test
    void getAllPhotoRepertoireEvents() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.EVENTS);

        //then
        System.out.println(allSeanceRepertoire);
        for (SeanceRepertoire SeanceRepertoire : allSeanceRepertoire) {
            System.out.println(SeanceRepertoire.toString());
        }
        assertEquals(2, allSeanceRepertoire.size());
    }

    // recuperer uniquement les photo repertoire AllIn du SeanceRepertoire
    @Test
    void getAllPhotoRepertoireAllInFromSeanceRepertoire() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> seanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);
//        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(seanceRepertoire.get(0));

        //then
        System.out.println(seanceRepertoire);
        for (SeanceRepertoire SeanceRepertoire : seanceRepertoire) {
            System.out.println(SeanceRepertoire.toString());
        }
        assertEquals(2, seanceRepertoire.size());
    }

    // recuperer uniquement les photo repertoire AllIn
    @Test
    void getAllPhotoRepertoireAllIn() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);

        //then
        System.out.println(allSeanceRepertoire);
        for (SeanceRepertoire SeanceRepertoire : allSeanceRepertoire) {
            System.out.println(SeanceRepertoire.toString());
        }
        assertEquals(2, allSeanceRepertoire.size());
    }

    // recuperer uniquement le Repertoires de travail de assitant
    @Test
    void getAssistantWorkRepertoire() {
        //given
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ASSISTANT_WORK);

        //then
        System.out.println(allSeanceRepertoire);
        for (SeanceRepertoire seanceRepertoire : allSeanceRepertoire) {
            System.out.println(seanceRepertoire);
        }
        assertEquals(1, allSeanceRepertoire.size());
        assertEquals(".\\src\\test\\resources\\10-Assistant_work\\2023_04_08_(00026)", allSeanceRepertoire.get(0).getPath());
    }

    // recupere un list de photo depuis un repertoire
    @Test
    void getAllPhotoFromAllIn() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);
        List<Photo> allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoire(assistantRepertoire);

        //then
        System.out.println(allPhotoFromSeanceRepertoire);
        for (Photo photo : allPhotoFromSeanceRepertoire) {
            System.out.println(photo);
        }
        assertEquals(7, allPhotoFromSeanceRepertoire.size());
    }

    // recupere un list de photo depuis un repertoire
    @Test
    void getAllPhotoFromEventFromPhotoRepertoire() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.EVENTS);
        List<Photo> allPhotoFromPhotoRepertoire0 = rootRep.getAllPhotoFromPhotoRepertoire(assistantRepertoire.get(0));
        List<Photo> allPhotoFromPhotoRepertoire1 = rootRep.getAllPhotoFromPhotoRepertoire(assistantRepertoire.get(1));

        //then
        System.out.println(allPhotoFromPhotoRepertoire0);
        for (Photo photo : allPhotoFromPhotoRepertoire0) {
            System.out.println(photo);
        }
        assertEquals(3, allPhotoFromPhotoRepertoire0.size());

        System.out.println(allPhotoFromPhotoRepertoire1);
        for (Photo photo : allPhotoFromPhotoRepertoire1) {
            System.out.println(photo);
        }
        assertEquals(1, allPhotoFromPhotoRepertoire1.size());
    }


    // recupere un list de photo depuis et le sauvegarder dans un json
    @Test
    void getAllPhotoFromAllInToJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);
        String jsonDest = mockConfig.getRootPath() + "/getAllPhotoFromAllInToJsonTEST.json";

        //when
        List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);
        List<Photo> allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoireToJson(assistantRepertoire, jsonDest);

        //then
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonDest);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + allPhotoFromSeanceRepertoire.size() + " <-- ");
        assertEquals(allPhotoFromSeanceRepertoireFromFile.size(), allPhotoFromSeanceRepertoire.size());
    }

    // recuperate un list de photo depuis un fichier json
    @Test
    void getAllPhotoFromAllInFromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);
        String jsonDest = mockConfig.getRootPath() + "/getAllPhotoFromAllInToJsonTEST.json";

        //when
        List<Photo> allPhotoFromJson = rootRep.getAllPhotoFromJson(jsonDest);

        //then
        System.out.println(allPhotoFromJson);
        for (Photo photo : allPhotoFromJson) {
            System.out.println(photo);
        }
        assertEquals(7, allPhotoFromJson.size());
    }

    // recupere un list de photo depuis le repertoire reel
    @Disabled("Not a real test, use to create Json getAllPhotoFromAllInRealToJsonTEST from real data ")
    @Test
    void getAllPhotoFromAllInRealToJson() {
        //given
        mockConfig.setRootPath("\\\\192.212.5.111\\80-Photo\\");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);
        String jsonDest = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST-out.json";

        //when
        List<SeanceRepertoire> assistantRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ALL_IN);
        List<Photo> allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoireToJson(assistantRepertoire, jsonDest);

        //then
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonDest);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + allPhotoFromSeanceRepertoire.size() + " <-- ");
        assertEquals(allPhotoFromSeanceRepertoireFromFile.size(), allPhotoFromSeanceRepertoire.size());
    }

    // recupere un list de photo depuis un repertoire
    //@Disabled("doesnt work on jenkins")
    @Test
    void getAllPhotoFromSeance() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);

        //when
        List<SeanceRepertoire> assistantRepertoires = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.EVENTS);
        List<Photo> allPhotoFromSeanceRepertoire = rootRep.getAllPhotoFromSeanceRepertoire(assistantRepertoires.get(0));

        //then
        System.out.println(assistantRepertoires);
        System.out.println(allPhotoFromSeanceRepertoire);
        for (Photo photo : allPhotoFromSeanceRepertoire) {
            System.out.println(photo);
        }
        assertEquals(3, allPhotoFromSeanceRepertoire.size());
    }

    // Group photo
    @Test
    void getGroupOfPhotoFromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);
        String jsonSrc = mockConfig.getRootPath() + "/getAllPhotoFromAllInRealToJsonTEST.json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonSrc);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        List<GroupOfPhotos> repGroupOfPhotoFrom = rootRep.getGroupOfPhotoFrom(allPhotoFromSeanceRepertoireFromFile);

        //then
        System.out.println(" --> " + allPhotoFromSeanceRepertoireFromFile.size() + " == " + repGroupOfPhotoFrom.size() + " <-- ");
        assertEquals(145, repGroupOfPhotoFrom.size());
        assertEquals(5, repGroupOfPhotoFrom.get(0).size());
        assertEquals(8, repGroupOfPhotoFrom.get(1).size());
        assertEquals(26, repGroupOfPhotoFrom.get(2).size());
        System.out.println(repGroupOfPhotoFrom.get(2).toString());
        assertEquals(13, repGroupOfPhotoFrom.get(3).size());
        assertEquals(12, repGroupOfPhotoFrom.get(4).size());
        assertEquals(6161, repGroupOfPhotoFrom.get(repGroupOfPhotoFrom.size()-1).size());

        //given
        String jsonDest = mockConfig.getRootPath() + "/getGroupOfPhotoTEST-out.json";
        WorkWithFile.putIntoJsonFile(repGroupOfPhotoFrom.get(2), jsonDest);

    }

    // Move Group photo
    //@Disabled("doesnt work on jenkins")
    @Test
    void regroupGroupOfPhotoFromJson() {
        //given
        Path rootTest = Paths.get("src", "test", "resources");
        mockConfig.setRootPath("./" + rootTest.toString() + "/");
        RootRepertoire rootRep = new RootRepertoire(mockConfig);
        String jsonSrc = mockConfig.getRootPath() + "/getGroupOfPhotoTEST.json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonSrc);
        GroupOfPhotos groupOfPhotoFrom = new GroupOfPhotos();
        try {
            groupOfPhotoFrom = objectMapper.readValue(file, new TypeReference<GroupOfPhotos>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //when
        List<SeanceRepertoire> allSeanceRepertoire = rootRep.getAllSeanceRepertoire(SeanceTypeEnum.ASSISTANT_WORK);
        int ret = rootRep.moveGroupToAssistantWork(mockConfig.getRootPath() + allSeanceRepertoire.get(0).getPath(), groupOfPhotoFrom ,true);

        //then
        System.out.println(allSeanceRepertoire);
        assertEquals(26, groupOfPhotoFrom.size());
        assertEquals(26, ret);
    }
}

