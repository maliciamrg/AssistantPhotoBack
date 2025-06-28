package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.dto.UpdateRepertoireNameRequestDto;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
import com.malicia.mrg.assistant.photo.service.RootRepertoire;
import com.malicia.mrg.assistant.photo.service.TagService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = {
        HealthContributorAutoConfiguration.class,
        HealthEndpointAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
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
    @MockBean
    private TagService tagService;

    @Autowired
    private RootRepertoire rootRep;
    @Autowired
    private PhotoshootService photoshootService;
    @Autowired
    private MyConfig myConfig;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.get(anyString())).thenReturn(null);
        doNothing().when(redisTemplate).set(anyString(), any());
        when(tagService.getTagListByName("00_EVENT")).thenReturn(Arrays.asList("fete","spectacle"));
        when(tagService.getTagListByName("00_WHERE")).thenReturn(Arrays.asList("maison","antony"));
        when(tagService.getTagListByName("00_WHAT")).thenReturn(Arrays.asList("train", "boat","laureline"));
        when(tagService.getTagListByName("00_WHO")).thenReturn(Arrays.asList("bob", "franck"));
    }

    @Test
    void testGetPhotoshoot_notFound() throws Exception {

        mockMvc.perform(get("/api/photoshoot/ALL_IN/session1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void validatePhotoshootName_shouldReturnValid_whenInputIsCorrect() throws Exception {

        mockMvc.perform(get("/api/photoshoot/EVENTS/2023-10-27_spectacle_antony_laureline/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("valid"));

    }

    @Test
    void updateRepertoireName_shouldReturnSuccessResponse() throws Exception {
        UpdateRepertoireNameRequestDto request = new UpdateRepertoireNameRequestDto();
        request.setPhotoshootNameNew("2023-10-27_fete_maison_bob");

        mockMvc.perform(put("/api/photoshoot/EVENTS/2023-10-27_spectacle_antony_laureline/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoshootName").value("2023-10-27_spectacle_antony_laureline"))
                .andExpect(jsonPath("$.photoshootNameNew").value("2023-10-27_fete_maison_bob"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Repertoire name updated successfully.")));
    }

    @Test
    void updateRepertoireName_shouldReturnInvalid_whenWrongPartCount() throws Exception {
        UpdateRepertoireNameRequestDto request = new UpdateRepertoireNameRequestDto();
        request.setPhotoshootNameNew("new_name");

        mockMvc.perform(put("/api/photoshoot/EVENTS/2023-10-27_spectacle_antony_laureline/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoshootName").value("2023-10-27_spectacle_antony_laureline"))
                .andExpect(jsonPath("$.photoshootNameNew").value("new_name"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("2 champs pour 4 attendu")));
    }

}
