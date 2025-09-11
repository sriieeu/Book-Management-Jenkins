package com.example.book_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Controller Tests")
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        objectMapper = new ObjectMapper();

        testBook = new Book("Test Book", "Test Author");
        testBook.setId(1L);

        testBooks = Arrays.asList(
            new Book("Book 1", "Author 1"),
            new Book("Book 2", "Author 2"),
            new Book("Book 3", "Author 3")
        );

        testBooks.get(0).setId(1L);
        testBooks.get(1).setId(2L);
        testBooks.get(2).setId(3L);
    }

    @Nested
    @DisplayName("Add Book Tests")
    class AddBookTests {

        @Test
        @DisplayName("Should add book successfully")
        void shouldAddBookSuccessfully() throws Exception {
            // Given
            Book bookToAdd = new Book("New Book", "New Author");
            when(bookService.addBook(any(Book.class))).thenReturn(testBook);

            // When & Then
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookToAdd)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testBook.getId()))
                    .andExpect(jsonPath("$.title").value(testBook.getTitle()))
                    .andExpect(jsonPath("$.author").value(testBook.getAuthor()));

            verify(bookService, times(1)).addBook(any(Book.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid book")
        void shouldReturnBadRequestForInvalidBook() throws Exception {
            // Given
            Book invalidBook = new Book("", ""); // Empty title and author

            // When & Then
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidBook)))
                    .andExpect(status().isBadRequest());

            verify(bookService, never()).addBook(any(Book.class));
        }

        @Test
        @DisplayName("Should handle service exception")
        void shouldHandleServiceException() throws Exception {
            // Given
            Book bookToAdd = new Book("New Book", "New Author");
            when(bookService.addBook(any(Book.class))).thenThrow(new RuntimeException("Service error"));

            // When & Then
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookToAdd)))
                    .andExpect(status().isInternalServerError());

            verify(bookService, times(1)).addBook(any(Book.class));
        }
    }

    @Nested
    @DisplayName("Get All Books Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return all books")
        void shouldReturnAllBooks() throws Exception {
            // Given
            when(bookService.getAllBooks()).thenReturn(testBooks);

            // When & Then
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(testBooks.get(0).getId()))
                    .andExpect(jsonPath("$[0].title").value(testBooks.get(0).getTitle()))
                    .andExpect(jsonPath("$[0].author").value(testBooks.get(0).getAuthor()))
                    .andExpect(jsonPath("$[1].id").value(testBooks.get(1).getId()))
                    .andExpect(jsonPath("$[1].title").value(testBooks.get(1).getTitle()))
                    .andExpect(jsonPath("$[1].author").value(testBooks.get(1).getAuthor()));

            verify(bookService, times(1)).getAllBooks();
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void shouldReturnEmptyListWhenNoBooksExist() throws Exception {
            // Given
            when(bookService.getAllBooks()).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(bookService, times(1)).getAllBooks();
        }
    }

    @Nested
    @DisplayName("Get Book By ID Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void shouldReturnBookWhenFound() throws Exception {
            // Given
            Long bookId = 1L;
            when(bookService.getBookById(bookId)).thenReturn(Optional.of(testBook));

            // When & Then
            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testBook.getId()))
                    .andExpect(jsonPath("$.title").value(testBook.getTitle()))
                    .andExpect(jsonPath("$.author").value(testBook.getAuthor()));

            verify(bookService, times(1)).getBookById(bookId);
        }

        @Test
        @DisplayName("Should return not found when book doesn't exist")
        void shouldReturnNotFoundWhenBookDoesntExist() throws Exception {
            // Given
            Long bookId = 999L;
            when(bookService.getBookById(bookId)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/books/{id}", bookId))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).getBookById(bookId);
        }
    }

    @Nested
    @DisplayName("Search Books Tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Should search books by keyword")
        void shouldSearchBooksByKeyword() throws Exception {
            // Given
            String keyword = "test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookService.searchBooks(keyword)).thenReturn(searchResults);

            // When & Then
            mockMvc.perform(get("/api/books/search")
                    .param("keyword", keyword))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(testBook.getId()))
                    .andExpect(jsonPath("$[0].title").value(testBook.getTitle()))
                    .andExpect(jsonPath("$[0].author").value(testBook.getAuthor()));

            verify(bookService, times(1)).searchBooks(keyword);
        }

        @Test
        @DisplayName("Should return empty list when no search results")
        void shouldReturnEmptyListWhenNoSearchResults() throws Exception {
            // Given
            String keyword = "nonexistent";
            when(bookService.searchBooks(keyword)).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/books/search")
                    .param("keyword", keyword))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(bookService, times(1)).searchBooks(keyword);
        }

        @Test
        @DisplayName("Should handle missing keyword parameter")
        void shouldHandleMissingKeywordParameter() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/books/search"))
                    .andExpect(status().isBadRequest());

            verify(bookService, never()).searchBooks(anyString());
        }
    }

    @Nested
    @DisplayName("Search Books By Title Tests")
    class SearchBooksByTitleTests {

        @Test
        @DisplayName("Should search books by title")
        void shouldSearchBooksByTitle() throws Exception {
            // Given
            String title = "test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookService.searchBooksByTitle(title)).thenReturn(searchResults);

            // When & Then
            mockMvc.perform(get("/api/books/search/title")
                    .param("title", title))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(testBook.getId()));

            verify(bookService, times(1)).searchBooksByTitle(title);
        }
    }

    @Nested
    @DisplayName("Search Books By Author Tests")
    class SearchBooksByAuthorTests {

        @Test
        @DisplayName("Should search books by author")
        void shouldSearchBooksByAuthor() throws Exception {
            // Given
            String author = "test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookService.searchBooksByAuthor(author)).thenReturn(searchResults);

            // When & Then
            mockMvc.perform(get("/api/books/search/author")
                    .param("author", author))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(testBook.getId()));

            verify(bookService, times(1)).searchBooksByAuthor(author);
        }
    }

    @Nested
    @DisplayName("Update Book Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book successfully")
        void shouldUpdateBookSuccessfully() throws Exception {
            // Given
            Long bookId = 1L;
            Book bookDetails = new Book("Updated Title", "Updated Author");
            Book updatedBook = new Book("Updated Title", "Updated Author");
            updatedBook.setId(bookId);

            when(bookService.updateBook(eq(bookId), any(Book.class))).thenReturn(updatedBook);

            // When & Then
            mockMvc.perform(put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(updatedBook.getId()))
                    .andExpect(jsonPath("$.title").value(updatedBook.getTitle()))
                    .andExpect(jsonPath("$.author").value(updatedBook.getAuthor()));

            verify(bookService, times(1)).updateBook(eq(bookId), any(Book.class));
        }

        @Test
        @DisplayName("Should return not found when book doesn't exist")
        void shouldReturnNotFoundWhenBookDoesntExist() throws Exception {
            // Given
            Long bookId = 999L;
            Book bookDetails = new Book("Updated Title", "Updated Author");
            when(bookService.updateBook(eq(bookId), any(Book.class))).thenReturn(null);

            // When & Then
            mockMvc.perform(put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookDetails)))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).updateBook(eq(bookId), any(Book.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid book details")
        void shouldReturnBadRequestForInvalidBookDetails() throws Exception {
            // Given
            Long bookId = 1L;
            Book invalidBook = new Book("", ""); // Empty title and author

            // When & Then
            mockMvc.perform(put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidBook)))
                    .andExpect(status().isBadRequest());

            verify(bookService, never()).updateBook(anyLong(), any(Book.class));
        }
    }

    @Nested
    @DisplayName("Delete Book Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book successfully")
        void shouldDeleteBookSuccessfully() throws Exception {
            // Given
            Long bookId = 1L;
            when(bookService.deleteBook(bookId)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/books/{id}", bookId))
                    .andExpect(status().isNoContent());

            verify(bookService, times(1)).deleteBook(bookId);
        }

        @Test
        @DisplayName("Should return not found when book doesn't exist")
        void shouldReturnNotFoundWhenBookDoesntExist() throws Exception {
            // Given
            Long bookId = 999L;
            when(bookService.deleteBook(bookId)).thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/api/books/{id}", bookId))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).deleteBook(bookId);
        }
    }

    @Nested
    @DisplayName("Test Endpoint")
    class TestEndpoint {

        @Test
        @DisplayName("Should return test message")
        void shouldReturnTestMessage() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/books/test"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("API is working!"));
        }
    }
}
