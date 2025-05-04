package com.malicia.mrg.assistant.photo;

import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyConfigTest {

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
    private MyConfig mockConfig; // Mocking the MyConfig dependency

    @Test
    void test_getName() {
        assertEquals("assistant", mockConfig.getName());
    }

}