package com.example.book_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName("Book Repository Tests")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book testBook1;
    private Book testBook2;
    private Book testBook3;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        testBook1 = new Book("Java Programming", "John Doe");
        testBook2 = new Book("Python Basics", "Jane Smith");
        testBook3 = new Book("Advanced Java", "John Doe");

        testBook1 = bookRepository.save(testBook1);
        testBook2 = bookRepository.save(testBook2);
        testBook3 = bookRepository.save(testBook3);
    }

    @Test
    @DisplayName("Should save book successfully")
    void shouldSaveBookSuccessfully() {
        // Given
        Book newBook = new Book("New Book", "New Author");

        // When
        Book savedBook = bookRepository.save(newBook);

        // Then
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("New Book", savedBook.getTitle());
        assertEquals("New Author", savedBook.getAuthor());
    }

    @Test
    @DisplayName("Should find book by ID")
    void shouldFindBookById() {
        // When
        Optional<Book> foundBook = bookRepository.findById(testBook1.getId());

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(testBook1.getTitle(), foundBook.get().getTitle());
        assertEquals(testBook1.getAuthor(), foundBook.get().getAuthor());
    }

    @Test
    @DisplayName("Should return empty when book not found by ID")
    void shouldReturnEmptyWhenBookNotFoundById() {
        // When
        Optional<Book> foundBook = bookRepository.findById(999L);

        // Then
        assertFalse(foundBook.isPresent());
    }

    @Test
    @DisplayName("Should find all books")
    void shouldFindAllBooks() {
        // When
        List<Book> allBooks = bookRepository.findAll();

        // Then
        assertEquals(3, allBooks.size());
        assertTrue(allBooks.stream().anyMatch(book -> book.getTitle().equals("Java Programming")));
        assertTrue(allBooks.stream().anyMatch(book -> book.getTitle().equals("Python Basics")));
        assertTrue(allBooks.stream().anyMatch(book -> book.getTitle().equals("Advanced Java")));
    }

    @Test
    @DisplayName("Should find books by title containing")
    void shouldFindBooksByTitleContaining() {
        // When
        List<Book> javaBooks = bookRepository.findByTitleContainingIgnoreCase("java");

        // Then
        assertEquals(2, javaBooks.size());
        assertTrue(javaBooks.stream().allMatch(book -> 
            book.getTitle().toLowerCase().contains("java")));
    }

    @Test
    @DisplayName("Should find books by author containing")
    void shouldFindBooksByAuthorContaining() {
        // When
        List<Book> johnBooks = bookRepository.findByAuthorContainingIgnoreCase("john");

        // Then
        assertEquals(2, johnBooks.size());
        assertTrue(johnBooks.stream().allMatch(book -> 
            book.getAuthor().toLowerCase().contains("john")));
    }

    @Test
    @DisplayName("Should find books by title or author containing")
    void shouldFindBooksByTitleOrAuthorContaining() {
        // When
        List<Book> javaOrJohnBooks = bookRepository.findByTitleOrAuthorContaining("java");

        // Then
        assertEquals(2, javaOrJohnBooks.size());
        assertTrue(javaOrJohnBooks.stream().anyMatch(book -> 
            book.getTitle().toLowerCase().contains("java")));
    }

    @Test
    @DisplayName("Should update book successfully")
    void shouldUpdateBookSuccessfully() {
        // Given
        testBook1.setTitle("Updated Java Programming");
        testBook1.setAuthor("Updated John Doe");

        // When
        Book updatedBook = bookRepository.save(testBook1);

        // Then
        assertEquals("Updated Java Programming", updatedBook.getTitle());
        assertEquals("Updated John Doe", updatedBook.getAuthor());
        assertEquals(testBook1.getId(), updatedBook.getId());
    }

    @Test
    @DisplayName("Should delete book successfully")
    void shouldDeleteBookSuccessfully() {
        // Given
        Long bookId = testBook1.getId();

        // When
        bookRepository.deleteById(bookId);

        // Then
        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertFalse(deletedBook.isPresent());
        assertEquals(2, bookRepository.findAll().size());
    }

    @Test
    @DisplayName("Should check if book exists")
    void shouldCheckIfBookExists() {
        // When & Then
        assertTrue(bookRepository.existsById(testBook1.getId()));
        assertFalse(bookRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should count books")
    void shouldCountBooks() {
        // When
        long count = bookRepository.count();

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should handle case insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        // When
        List<Book> javaBooks = bookRepository.findByTitleContainingIgnoreCase("JAVA");
        List<Book> johnBooks = bookRepository.findByAuthorContainingIgnoreCase("JOHN");

        // Then
        assertEquals(2, javaBooks.size());
        assertEquals(2, johnBooks.size());
    }

    @Test
    @DisplayName("Should handle empty search results")
    void shouldHandleEmptySearchResults() {
        // When
        List<Book> noResults = bookRepository.findByTitleContainingIgnoreCase("nonexistent");

        // Then
        assertTrue(noResults.isEmpty());
    }
}
