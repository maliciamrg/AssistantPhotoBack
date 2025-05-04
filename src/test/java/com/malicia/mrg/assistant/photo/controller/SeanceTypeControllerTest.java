package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.DTO.SeanceTypeDto;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SeanceTypeControllerTest {

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
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MyConfig config;
    @Autowired
    private RootRepertoire rootRep;

    private MockMvc mockMvc;


    @Test
    void getSeanceTypes_ShouldReturnSeanceTypes() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        PhotoSessionController controller = new PhotoSessionController(config,rootRep,redisTemplate);
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
}
