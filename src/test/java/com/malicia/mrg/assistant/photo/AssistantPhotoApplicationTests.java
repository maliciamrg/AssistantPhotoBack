package com.malicia.mrg.assistant.photo;

import com.malicia.mrg.assistant.photo.service.PhotoService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class  AssistantPhotoApplicationTests {

	@MockBean
	private PhotoService photoService;

	@Test
	void contextLoads() { //calls created in advance wait test implementation
	}

}
