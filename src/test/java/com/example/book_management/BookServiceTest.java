package com.example.book_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
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
        void shouldAddBookSuccessfully() {
            // Given
            Book bookToAdd = new Book("New Book", "New Author");
            when(bookRepository.save(any(Book.class))).thenReturn(testBook);

            // When
            Book result = bookService.addBook(bookToAdd);

            // Then
            assertNotNull(result);
            assertEquals(testBook.getId(), result.getId());
            assertEquals(testBook.getTitle(), result.getTitle());
            assertEquals(testBook.getAuthor(), result.getAuthor());
            verify(bookRepository, times(1)).save(bookToAdd);
        }

        @Test
        @DisplayName("Should handle null book")
        void shouldHandleNullBook() {
            // Given
            when(bookRepository.save(null)).thenThrow(new IllegalArgumentException("Book cannot be null"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bookService.addBook(null);
            });
        }

        @Test
        @DisplayName("Should handle repository exception")
        void shouldHandleRepositoryException() {
            // Given
            Book bookToAdd = new Book("New Book", "New Author");
            when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                bookService.addBook(bookToAdd);
            });
        }
    }

    @Nested
    @DisplayName("Get All Books Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return all books")
        void shouldReturnAllBooks() {
            // Given
            when(bookRepository.findAll()).thenReturn(testBooks);

            // When
            List<Book> result = bookService.getAllBooks();

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(testBooks, result);
            verify(bookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void shouldReturnEmptyListWhenNoBooksExist() {
            // Given
            when(bookRepository.findAll()).thenReturn(Arrays.asList());

            // When
            List<Book> result = bookService.getAllBooks();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(bookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle repository exception")
        void shouldHandleRepositoryException() {
            // Given
            when(bookRepository.findAll()).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                bookService.getAllBooks();
            });
        }
    }

    @Nested
    @DisplayName("Get Book By ID Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void shouldReturnBookWhenFound() {
            // Given
            Long bookId = 1L;
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

            // When
            Optional<Book> result = bookService.getBookById(bookId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(testBook, result.get());
            verify(bookRepository, times(1)).findById(bookId);
        }

        @Test
        @DisplayName("Should return empty when book not found")
        void shouldReturnEmptyWhenBookNotFound() {
            // Given
            Long bookId = 999L;
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

            // When
            Optional<Book> result = bookService.getBookById(bookId);

            // Then
            assertFalse(result.isPresent());
            verify(bookRepository, times(1)).findById(bookId);
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            // Given
            when(bookRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bookService.getBookById(null);
            });
        }
    }

    @Nested
    @DisplayName("Search Books Tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Should search books by keyword")
        void shouldSearchBooksByKeyword() {
            // Given
            String keyword = "test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookRepository.findByTitleOrAuthorContaining(keyword))
                .thenReturn(searchResults);

            // When
            List<Book> result = bookService.searchBooks(keyword);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testBook, result.get(0));
            verify(bookRepository, times(1))
                .findByTitleOrAuthorContaining(keyword);
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void shouldReturnEmptyListWhenNoMatchesFound() {
            // Given
            String keyword = "nonexistent";
            when(bookRepository.findByTitleOrAuthorContaining(keyword))
                .thenReturn(Arrays.asList());

            // When
            List<Book> result = bookService.searchBooks(keyword);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle empty keyword")
        void shouldHandleEmptyKeyword() {
            // Given
            String keyword = "";
            when(bookRepository.findByTitleOrAuthorContaining(keyword))
                .thenReturn(testBooks);

            // When
            List<Book> result = bookService.searchBooks(keyword);

            // Then
            assertNotNull(result);
            assertEquals(testBooks, result);
        }

        @Test
        @DisplayName("Should handle null keyword")
        void shouldHandleNullKeyword() {
            // Given
            when(bookRepository.findByTitleOrAuthorContaining(null))
                .thenReturn(Arrays.asList());

            // When
            List<Book> result = bookService.searchBooks(null);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Search Books By Title Tests")
    class SearchBooksByTitleTests {

        @Test
        @DisplayName("Should search books by title")
        void shouldSearchBooksByTitle() {
            // Given
            String title = "Test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookRepository.findByTitleContainingIgnoreCase(title)).thenReturn(searchResults);

            // When
            List<Book> result = bookService.searchBooksByTitle(title);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testBook, result.get(0));
            verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(title);
        }

        @Test
        @DisplayName("Should return empty list when no title matches found")
        void shouldReturnEmptyListWhenNoTitleMatchesFound() {
            // Given
            String title = "nonexistent";
            when(bookRepository.findByTitleContainingIgnoreCase(title)).thenReturn(Arrays.asList());

            // When
            List<Book> result = bookService.searchBooksByTitle(title);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Search Books By Author Tests")
    class SearchBooksByAuthorTests {

        @Test
        @DisplayName("Should search books by author")
        void shouldSearchBooksByAuthor() {
            // Given
            String author = "Test";
            List<Book> searchResults = Arrays.asList(testBook);
            when(bookRepository.findByAuthorContainingIgnoreCase(author)).thenReturn(searchResults);

            // When
            List<Book> result = bookService.searchBooksByAuthor(author);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testBook, result.get(0));
            verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase(author);
        }

        @Test
        @DisplayName("Should return empty list when no author matches found")
        void shouldReturnEmptyListWhenNoAuthorMatchesFound() {
            // Given
            String author = "nonexistent";
            when(bookRepository.findByAuthorContainingIgnoreCase(author)).thenReturn(Arrays.asList());

            // When
            List<Book> result = bookService.searchBooksByAuthor(author);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Book Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book successfully")
        void shouldUpdateBookSuccessfully() {
            // Given
            Long bookId = 1L;
            Book bookDetails = new Book("Updated Title", "Updated Author");
            Book updatedBook = new Book("Updated Title", "Updated Author");
            updatedBook.setId(bookId);
            
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
            when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

            // When
            Book result = bookService.updateBook(bookId, bookDetails);

            // Then
            assertNotNull(result);
            assertEquals(updatedBook.getTitle(), result.getTitle());
            assertEquals(updatedBook.getAuthor(), result.getAuthor());
            verify(bookRepository, times(1)).findById(bookId);
            verify(bookRepository, times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("Should return null when book not found")
        void shouldReturnNullWhenBookNotFound() {
            // Given
            Long bookId = 999L;
            Book bookDetails = new Book("Updated Title", "Updated Author");
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

            // When
            Book result = bookService.updateBook(bookId, bookDetails);

            // Then
            assertNull(result);
            verify(bookRepository, times(1)).findById(bookId);
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should handle null book details")
        void shouldHandleNullBookDetails() {
            // Given
            Long bookId = 1L;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bookService.updateBook(bookId, null);
            });
        }
    }

    @Nested
    @DisplayName("Delete Book Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book successfully")
        void shouldDeleteBookSuccessfully() {
            // Given
            Long bookId = 1L;
            when(bookRepository.existsById(bookId)).thenReturn(true);
            doNothing().when(bookRepository).deleteById(bookId);

            // When
            boolean result = bookService.deleteBook(bookId);

            // Then
            assertTrue(result);
            verify(bookRepository, times(1)).existsById(bookId);
            verify(bookRepository, times(1)).deleteById(bookId);
        }

        @Test
        @DisplayName("Should return false when book not found")
        void shouldReturnFalseWhenBookNotFound() {
            // Given
            Long bookId = 999L;
            when(bookRepository.existsById(bookId)).thenReturn(false);

            // When
            boolean result = bookService.deleteBook(bookId);

            // Then
            assertFalse(result);
            verify(bookRepository, times(1)).existsById(bookId);
            verify(bookRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            // Given
            when(bookRepository.existsById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bookService.deleteBook(null);
            });
        }
    }
}
