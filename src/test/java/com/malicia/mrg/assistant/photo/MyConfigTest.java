package com.malicia.mrg.assistant.photo;

import com.malicia.mrg.assistant.photo.service.PhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MyConfigTest {


    @MockBean
    private PhotoService photoService;

    @Autowired
    private MyConfig mockConfig; // Mocking the MyConfig dependency

    @Test
    void test_getName() {
        assertEquals("assistant", mockConfig.getName());
    }

}