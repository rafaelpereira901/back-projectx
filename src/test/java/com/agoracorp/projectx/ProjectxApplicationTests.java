package com.agoracorp.projectx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
	"cloudinary.cloud-name=test",
	"cloudinary.api-key=test",
	"cloudinary.api-secret=test"
})
class ProjectxApplicationTests {

	@Test
	void contextLoads() {
	}

}
