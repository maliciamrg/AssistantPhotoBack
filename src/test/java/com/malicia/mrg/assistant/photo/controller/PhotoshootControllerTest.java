package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.service.FileSystemService;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootType;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootTypeEnum;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
class PhotoshootControllerTest {


    @MockBean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    @MockBean
    private CacheService redisTemplate;
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private ReactiveHealthContributor redisHealthContributor;
    @Autowired
    private RootRepertoire rootRep;
    @Autowired
    private PhotoshootService photoshootService;
    @Autowired
    private MyConfig myConfig;
    @Autowired
    private PhotoshootController photoshootController;


    private MockMvc mockMvc;
    @Autowired
    private PhotoshootTypeController photoshootTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.get(anyString())).thenReturn(null);
        doNothing().when(redisTemplate).set(anyString(), any(), any());
    }


    @Test
    void getSeanceTypes_ShouldReturnSeanceTypes() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        PhotoshootController controller = new PhotoshootController(myConfig, photoshootService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //given
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/getSeanceTypes_ShouldReturnSeanceTypesTEST.json";

        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(get("/api/photoshoot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(PhotoshootTypeEnum.values().length)))
                .andExpect(jsonPath("$[0].id", is("ALL_IN")))
                .andExpect(jsonPath("$[0].name", is("ALL_IN")))
                .andReturn();


        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonResponse = result.getResponse().getContentAsString();
            // Map JSON string to List<PhotoshootTypeDto>
            List<PhotoshootType> seanceTypeList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            FileSystemService.putIntoJsonFile(seanceTypeList, jsonDest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    void getSeanceRepertoires_ShouldReturnSeanceRepertoires() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        PhotoshootController controller = new PhotoshootController(myConfig, photoshootService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //given
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/getSeanceRepertoires_ShouldReturnSeanceRepertoiresTEST.json";


        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(get("/api/photoshoot/ALL_IN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].name").value("Repertoire 1"))
                //              .andExpect(jsonPath("$[1].name").value("Repertoire 2"))
                .andReturn();


        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonResponse = result.getResponse().getContentAsString();
            // Map JSON string to List<PhotoshootTypeDto>
            List<Photoshoot> seanceTypeList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            FileSystemService.putIntoJsonFile(seanceTypeList, jsonDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetPhotoshootType() {
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetTypesDeSeanceTEST.json";

        List<PhotoshootType> result = photoshootTypeController.getPhotoshootType();

        assertEquals(7, result.size());
        assertEquals("ALL_IN", result.get(0).getNom().name());

        FileSystemService.putIntoJsonFile(result, jsonDest);
    }

    @Test
    void testGetPhotoshootByType() {
        // Initialize controller with the real RootRepertoire bean
        PhotoshootController controller = new PhotoshootController(myConfig, photoshootService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetSeancesParTypeTEST.json";

        List<Photoshoot> result = photoshootTypeController.getPhotoshootByType("ALL_IN");

        assertEquals(2, result.size());

        FileSystemService.putIntoJsonFile(result, jsonDest);
    }

    @Test
    void testGetPhotoshoot_success() {
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetPhotosDeSeance_successTEST.json";

        ResponseEntity<Photoshoot> response = photoshootController.getPhotoshoot("ALL_IN", "subOne");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(7, (response.getBody().getGroupOfPhoto().size()));

        FileSystemService.putIntoJsonFile(response, jsonDest);
    }

    @Test
    void testGetPhotoshoot_notFound() {
        when(photoshootService.getPhotoshootList("ALL_IN"))
                .thenThrow(new IllegalArgumentException("Not found"));

        ResponseEntity<Photoshoot> response = photoshootController.getPhotoshoot("ALL_IN", "session1");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
