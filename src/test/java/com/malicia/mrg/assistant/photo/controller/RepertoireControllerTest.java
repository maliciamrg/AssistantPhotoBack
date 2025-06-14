package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.dto.RepertoireNameValidationRequestDto;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import com.malicia.mrg.assistant.photo.service.PhotoSessionService;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
class RepertoireControllerTest {

    @MockBean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    @MockBean
    private CacheService redisTemplate;
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private ReactiveHealthContributor redisHealthContributor;
    @MockBean
    private TagService tagService;
    @MockBean
    private PhotoSessionService photoSessionService;

    @Autowired
    private MyConfig config;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.get(anyString())).thenReturn(null);
        doNothing().when(redisTemplate).set(anyString(), any(), any());
        when(tagService.getTagListByName("00_EVENT")).thenReturn(Collections.singletonList("fete"));
        when(tagService.getTagListByName("00_WHERE")).thenReturn(Collections.singletonList("maison"));
        when(tagService.getTagListByName("00_WHAT")).thenReturn(Arrays.asList("train", "boat"));
        when(tagService.getTagListByName("00_WHO")).thenReturn(Arrays.asList("bob", "franck"));
    }

    @Test
    void validateRepertoireName_shouldReturnValid_whenInputIsCorrect() throws Exception {

        RepertoireNameValidationRequestDto request = new RepertoireNameValidationRequestDto();
        request.setTypeName("EVENTS");
        request.setRepertoireName("2023-06-01_fete_maison_bob");

        RepertoireNameValidationRequestDto request2 = new RepertoireNameValidationRequestDto();
        request2.setTypeName("EVENTS");
        request2.setRepertoireName("2023-06-01_fete_maison_boat");

        when(photoSessionService.getSeanceRepertoireList("EVENTS"))
                .thenReturn(Collections.singletonList(new SeanceRepertoire()));

        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("lowerDate", "2023-06-01");
        when(photoSessionService.getMetaDataFromPhotoRepertoire(any(), any()))
                .thenReturn(metaData);

        mockMvc.perform(post("/api/repertoires/validate-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("valid"));

        mockMvc.perform(post("/api/repertoires/validate-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("valid"));
    }

    @Test
    void getRepertoireNameConfig_shouldReturnZoneValeurAdmise() throws Exception {

        mockMvc.perform(get("/api/repertoires/name/config/EVENTS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ZoneValeurAdmise").isArray())
                .andExpect(jsonPath("$.ZoneValeurAdmise[0]").isArray())
                .andExpect(jsonPath("$.ZoneValeurAdmise[1]").isArray());
    }

    @Test
    void updateRepertoireName_shouldReturnSuccessResponse() throws Exception {
        UpdateRepertoireNameRequestDto request = new UpdateRepertoireNameRequestDto();
        request.setRepertoireNameOld("old_name");
        request.setRepertoireNameNew("new_name");

        mockMvc.perform(put("/api/repertoires/MOCK_TYPE/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.old").value("old_name"))
                .andExpect(jsonPath("$.repertoireName").value("new_name"))
                .andExpect(jsonPath("$.message").value("Repertoire name updated successfully."));
    }

    @Test
    void validateRepertoireName_shouldReturnInvalid_whenWrongPartCount() throws Exception {
        RepertoireNameValidationRequestDto request = new RepertoireNameValidationRequestDto();
        request.setTypeName("EVENTS");
        request.setRepertoireName("ONLY_THREE_PART");

        mockMvc.perform(post("/api/repertoires/validate-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("3 champs pour 4 attendu"));
    }
}
