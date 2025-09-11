package com.example.book_management;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Book Management Application Tests")
class BookManagementApplicationTests {

	@Test
	@DisplayName("Should load application context successfully")
	void contextLoads() {
		// This test verifies that the Spring application context loads successfully
		// If there are any configuration issues, this test will fail
	}

}
