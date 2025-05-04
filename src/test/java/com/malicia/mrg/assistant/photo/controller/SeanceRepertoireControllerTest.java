package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SeanceRepertoireControllerTest {

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

    @Autowired
    private RootRepertoire rootRepertoire;

    private MockMvc mockMvc;

    @Test
    void getSeanceRepertoires_ShouldReturnSeanceRepertoires() throws Exception {
        // Initialize controller with the real RootRepertoire bean
        SeanceRepertoireController controller = new SeanceRepertoireController(rootRepertoire);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //given
        Path rootTest = Paths.get("src", "test", "resources");
        String jsonDest = "./" + rootTest + "/" + "/getSeanceRepertoires_ShouldReturnSeanceRepertoiresTEST.json";


        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(get("/api/seance-repertoire")
                        .param("type", "ALL_IN")
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
}
