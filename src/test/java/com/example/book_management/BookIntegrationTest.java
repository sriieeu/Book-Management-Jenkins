package com.example.book_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName("Book Integration Tests")
class BookIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        bookRepository.deleteAll(); // Clean up before each test
    }

    @Nested
    @DisplayName("CRUD Operations Integration Tests")
    class CrudOperationsIntegrationTests {

        @Test
        @DisplayName("Should perform full CRUD cycle")
        void shouldPerformFullCrudCycle() throws Exception {
            // Create
            Book bookToCreate = new Book("Integration Test Book", "Integration Test Author");
            String bookJson = objectMapper.writeValueAsString(bookToCreate);

            String response = mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bookJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("Integration Test Book"))
                    .andExpect(jsonPath("$.author").value("Integration Test Author"))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Book createdBook = objectMapper.readValue(response, Book.class);
            Long bookId = createdBook.getId();

            // Read
            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(bookId))
                    .andExpect(jsonPath("$.title").value("Integration Test Book"))
                    .andExpect(jsonPath("$.author").value("Integration Test Author"));

            // Update
            Book bookToUpdate = new Book("Updated Integration Test Book", "Updated Integration Test Author");
            String updateJson = objectMapper.writeValueAsString(bookToUpdate);

            mockMvc.perform(put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(bookId))
                    .andExpect(jsonPath("$.title").value("Updated Integration Test Book"))
                    .andExpect(jsonPath("$.author").value("Updated Integration Test Author"));

            // Verify update
            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Integration Test Book"))
                    .andExpect(jsonPath("$.author").value("Updated Integration Test Author"));

            // Delete
            mockMvc.perform(delete("/api/books/{id}", bookId))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle multiple books")
        void shouldHandleMultipleBooks() throws Exception {
            // Create multiple books
            Book book1 = new Book("Book 1", "Author 1");
            Book book2 = new Book("Book 2", "Author 2");
            Book book3 = new Book("Book 3", "Author 3");

            String book1Json = objectMapper.writeValueAsString(book1);
            String book2Json = objectMapper.writeValueAsString(book2);
            String book3Json = objectMapper.writeValueAsString(book3);

            // Create books
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book1Json))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book2Json))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book3Json))
                    .andExpect(status().isCreated());

            // Get all books
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                    .andExpect(jsonPath("$[0].title").value("Book 1"))
                    .andExpect(jsonPath("$[1].title").value("Book 2"))
                    .andExpect(jsonPath("$[2].title").value("Book 3"));
        }
    }

    @Nested
    @DisplayName("Search Integration Tests")
    class SearchIntegrationTests {

        @BeforeEach
        void setUpSearchData() throws Exception {
            // Create test data for search
            Book book1 = new Book("Java Programming", "John Doe");
            Book book2 = new Book("Python Basics", "Jane Smith");
            Book book3 = new Book("Advanced Java", "John Doe");
            Book book4 = new Book("JavaScript Guide", "Bob Johnson");

            String book1Json = objectMapper.writeValueAsString(book1);
            String book2Json = objectMapper.writeValueAsString(book2);
            String book3Json = objectMapper.writeValueAsString(book3);
            String book4Json = objectMapper.writeValueAsString(book4);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book1Json))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book2Json))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book3Json))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(book4Json))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should search books by keyword")
        void shouldSearchBooksByKeyword() throws Exception {
            // Search for "Java"
            mockMvc.perform(get("/api/books/search")
                    .param("keyword", "Java"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                    .andExpect(jsonPath("$[0].title").value("Java Programming"))
                    .andExpect(jsonPath("$[1].title").value("Advanced Java"))
                    .andExpect(jsonPath("$[2].title").value("JavaScript Guide"));

            // Search for "John"
            mockMvc.perform(get("/api/books/search")
                    .param("keyword", "John"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                    .andExpect(jsonPath("$[0].author").value("John Doe"))
                    .andExpect(jsonPath("$[1].author").value("John Doe"))
                    .andExpect(jsonPath("$[2].author").value("Bob Johnson"));
        }

        @Test
        @DisplayName("Should search books by title")
        void shouldSearchBooksByTitle() throws Exception {
            mockMvc.perform(get("/api/books/search/title")
                    .param("title", "Java"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                    .andExpect(jsonPath("$[0].title").value("Java Programming"))
                    .andExpect(jsonPath("$[1].title").value("Advanced Java"))
                    .andExpect(jsonPath("$[2].title").value("JavaScript Guide"));
        }

        @Test
        @DisplayName("Should search books by author")
        void shouldSearchBooksByAuthor() throws Exception {
            mockMvc.perform(get("/api/books/search/author")
                    .param("author", "John"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                    .andExpect(jsonPath("$[0].author").value("John Doe"))
                    .andExpect(jsonPath("$[1].author").value("John Doe"))
                    .andExpect(jsonPath("$[2].author").value("Bob Johnson"));
        }

        @Test
        @DisplayName("Should return empty results for non-existent search")
        void shouldReturnEmptyResultsForNonExistentSearch() throws Exception {
            mockMvc.perform(get("/api/books/search")
                    .param("keyword", "NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Validation Integration Tests")
    class ValidationIntegrationTests {

        @Test
        @DisplayName("Should reject book with empty title")
        void shouldRejectBookWithEmptyTitle() throws Exception {
            Book invalidBook = new Book("", "Valid Author");
            String invalidJson = objectMapper.writeValueAsString(invalidBook);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject book with empty author")
        void shouldRejectBookWithEmptyAuthor() throws Exception {
            Book invalidBook = new Book("Valid Title", "");
            String invalidJson = objectMapper.writeValueAsString(invalidBook);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject book with null title")
        void shouldRejectBookWithNullTitle() throws Exception {
            Book invalidBook = new Book(null, "Valid Author");
            String invalidJson = objectMapper.writeValueAsString(invalidBook);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject book with null author")
        void shouldRejectBookWithNullAuthor() throws Exception {
            Book invalidBook = new Book("Valid Title", null);
            String invalidJson = objectMapper.writeValueAsString(invalidBook);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Error Handling Integration Tests")
    class ErrorHandlingIntegrationTests {

        @Test
        @DisplayName("Should return not found for non-existent book")
        void shouldReturnNotFoundForNonExistentBook() throws Exception {
            mockMvc.perform(get("/api/books/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return not found when updating non-existent book")
        void shouldReturnNotFoundWhenUpdatingNonExistentBook() throws Exception {
            Book bookToUpdate = new Book("Updated Title", "Updated Author");
            String updateJson = objectMapper.writeValueAsString(bookToUpdate);

            mockMvc.perform(put("/api/books/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return not found when deleting non-existent book")
        void shouldReturnNotFoundWhenDeletingNonExistentBook() throws Exception {
            mockMvc.perform(delete("/api/books/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle missing search parameter")
        void shouldHandleMissingSearchParameter() throws Exception {
            mockMvc.perform(get("/api/books/search"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("API Endpoint Tests")
    class ApiEndpointTests {

        @Test
        @DisplayName("Should return test message")
        void shouldReturnTestMessage() throws Exception {
            mockMvc.perform(get("/api/books/test"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("API is working!"));
        }

        @Test
        @DisplayName("Should handle CORS preflight requests")
        void shouldHandleCorsPreflightRequests() throws Exception {
            mockMvc.perform(options("/api/books")
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "Content-Type"))
                    .andExpect(status().isOk());
        }
    }
}
