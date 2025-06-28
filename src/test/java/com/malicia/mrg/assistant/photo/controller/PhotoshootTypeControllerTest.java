package com.malicia.mrg.assistant.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.service.PhotoshootService;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
class PhotoshootTypeControllerTest {

    @MockBean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    @MockBean
    private CacheService redisTemplate;
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private ReactiveHealthContributor redisHealthContributor;


    @Autowired
    private PhotoshootService photoshootService;
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
        doNothing().when(redisTemplate).set(anyString(), any());

//        PhotoshootMetaData metaData = new PhotoshootMetaData();
//        metaData.setLowerDate("2023-06-01");
//        metaData.setNbDay(10);
//        metaData.setNbSelectedPhoto(10);
//        metaData.setNbStar(new int[]{0, 1, 1, 0, 0, 0});
//        when(photoshootService.getMetaDataFromPhotoshoot(any(), any()))
//                .thenReturn(metaData);
    }

    @Test
    void testGetPhotoshootType() throws Exception {
        mockMvc.perform(get("/api/photoshoot-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].photoshootTypeEnum").value("ALL_IN"))
                .andExpect(jsonPath("$[0].photoshootList[0].path").value("00-CheckIn"))
                .andDo(print());
    }

    @Test
    void testGetPhotoshootParam() throws Exception {
        mockMvc.perform(get("/api/photoshoot-type/EVENTS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoshootTypeEnum").value("EVENTS"))
                .andExpect(jsonPath("$.photoshootList.length()").value(1));
    }

    @Test
    void testGetPhotoshootParamMultiple() throws Exception {
        mockMvc.perform(get("/api/photoshoot-type/ALL_IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoshootTypeEnum").value("ALL_IN"))
                .andExpect(jsonPath("$.photoshootList.length()").value(2));
    }

    @Test
    void getPhotoshootParam_shouldReturnZoneValeurAdmise() throws Exception {

        mockMvc.perform(get("/api/photoshoot-type/EVENTS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zoneValeurAdmise").isArray())
                .andExpect(jsonPath("$.zoneValeurAdmise[0]").value("£DATE£"))
                .andExpect(jsonPath("$.zoneValeurAdmise[1]").value("@00_EVENT@"));
    }

    @Test
    void testGetPhotoshootByType() throws Exception {
        mockMvc.perform(get("/api/photoshoot-type/EVENTS/photoshoot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("2023-10-27_spectacle_antony_laureline"));
    }

}
