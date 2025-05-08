package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.DTO.SeanceTypeDto;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
public class PhotoSessionControllerTest {


    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private ReactiveHealthContributor redisHealthContributor;
    @MockBean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Autowired
    private RootRepertoire rootRep;
    @Autowired
    private PhotoSessionService photoSessionService;
    @Autowired
    private MyConfig myConfig;
    @Autowired
    private PhotoSessionController photoSessionController;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        doNothing().when(valueOperations).set(anyString(), any(), any());
    }


    @Test
    void getSeanceTypes_ShouldReturnSeanceTypes() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        PhotoSessionController controller = new PhotoSessionController(myConfig,photoSessionService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //given
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/getSeanceTypes_ShouldReturnSeanceTypesTEST.json";

        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(SeanceTypeEnum.values().length)))
                .andExpect(jsonPath("$[0].id", is("ALL_IN")))
                .andExpect(jsonPath("$[0].name", is("ALL_IN")))
                .andReturn();


        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonResponse = result.getResponse().getContentAsString();
            // Map JSON string to List<SeanceTypeDto>
            List<SeanceTypeDto> seanceTypeList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            WorkWithFile.putIntoJsonFile(seanceTypeList, jsonDest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    void getSeanceRepertoires_ShouldReturnSeanceRepertoires() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        PhotoSessionController controller = new PhotoSessionController(myConfig,photoSessionService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //given
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/getSeanceRepertoires_ShouldReturnSeanceRepertoiresTEST.json";


        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(get("/api/sessions/ALL_IN")
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
            // Map JSON string to List<SeanceTypeDto>
            List<SeanceRepertoire> seanceTypeList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            WorkWithFile.putIntoJsonFile(seanceTypeList, jsonDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetTypesDeSeance() {
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetTypesDeSeanceTEST.json";

        List<SeanceTypeDto> result = photoSessionController.getTypesDeSeance();

        assertEquals(7, result.size());
        assertEquals("ALL_IN", result.get(0).getId());

        WorkWithFile.putIntoJsonFile(result, jsonDest);
    }

    @Test
    void testGetSeancesParType() {
        // Initialize controller with the real RootRepertoire bean
        PhotoSessionController controller = new PhotoSessionController(myConfig,photoSessionService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetSeancesParTypeTEST.json";

        List<SeanceRepertoire> result = photoSessionController.getSeancesParType("ALL_IN");

        assertEquals(2, result.size());

        WorkWithFile.putIntoJsonFile(result, jsonDest);
    }

    @Test
    void testGetPhotosDeSeance_success() {
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/testGetPhotosDeSeance_successTEST.json";

        ResponseEntity<List<Photo>> response = photoSessionController.getPhotosDeSeance("ALL_IN", "subOne");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(7, response.getBody().size());

        WorkWithFile.putIntoJsonFile(response, jsonDest);
    }

    @Test
    void testGetPhotosDeSeance_notFound() {
        when(photoSessionService.getSeanceRepertoireList("ALL_IN"))
                .thenThrow(new IllegalArgumentException("Not found"));

        ResponseEntity<List<Photo>> response = photoSessionController.getPhotosDeSeance("ALL_IN", "session1");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
