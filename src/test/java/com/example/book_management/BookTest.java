package com.example.book_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Book Entity Tests")
class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create book with default constructor")
        void shouldCreateBookWithDefaultConstructor() {
            assertNotNull(book);
            assertNull(book.getId());
            assertNull(book.getTitle());
            assertNull(book.getAuthor());
        }

        @Test
        @DisplayName("Should create book with parameterized constructor")
        void shouldCreateBookWithParameterizedConstructor() {
            Book newBook = new Book("Test Title", "Test Author");
            
            assertNotNull(newBook);
            assertNull(newBook.getId()); // ID should be null until persisted
            assertEquals("Test Title", newBook.getTitle());
            assertEquals("Test Author", newBook.getAuthor());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get ID")
        void shouldSetAndGetId() {
            Long id = 1L;
            book.setId(id);
            assertEquals(id, book.getId());
        }

        @Test
        @DisplayName("Should set and get title")
        void shouldSetAndGetTitle() {
            String title = "Test Book Title";
            book.setTitle(title);
            assertEquals(title, book.getTitle());
        }

        @Test
        @DisplayName("Should set and get author")
        void shouldSetAndGetAuthor() {
            String author = "Test Author Name";
            book.setAuthor(author);
            assertEquals(author, book.getAuthor());
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            book.setId(null);
            book.setTitle(null);
            book.setAuthor(null);
            
            assertNull(book.getId());
            assertNull(book.getTitle());
            assertNull(book.getAuthor());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return correct string representation")
        void shouldReturnCorrectStringRepresentation() {
            book.setId(1L);
            book.setTitle("Test Book");
            book.setAuthor("Test Author");
            
            String expected = "Book{id=1, title='Test Book', author='Test Author'}";
            assertEquals(expected, book.toString());
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            String result = book.toString();
            assertTrue(result.contains("id=null"));
            assertTrue(result.contains("title='null'"));
            assertTrue(result.contains("author='null'"));
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal when all fields are same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            Book book1 = new Book("Test Book", "Test Author");
            Book book2 = new Book("Test Book", "Test Author");
            
            book1.setId(1L);
            book2.setId(1L);
            
            assertEquals(book1, book2);
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Book book1 = new Book("Test Book", "Test Author");
            Book book2 = new Book("Test Book", "Test Author");
            
            book1.setId(1L);
            book2.setId(2L);
            
            assertNotEquals(book1, book2);
        }

        @Test
        @DisplayName("Should not be equal when titles are different")
        void shouldNotBeEqualWhenTitlesAreDifferent() {
            Book book1 = new Book("Test Book 1", "Test Author");
            Book book2 = new Book("Test Book 2", "Test Author");
            
            book1.setId(1L);
            book2.setId(1L);
            
            assertNotEquals(book1, book2);
        }

        @Test
        @DisplayName("Should not be equal when authors are different")
        void shouldNotBeEqualWhenAuthorsAreDifferent() {
            Book book1 = new Book("Test Book", "Test Author 1");
            Book book2 = new Book("Test Book", "Test Author 2");
            
            book1.setId(1L);
            book2.setId(1L);
            
            assertNotEquals(book1, book2);
        }
    }

    @Nested
    @DisplayName("Hash Code Tests")
    class HashCodeTests {

        @Test
        @DisplayName("Should have same hash code for equal objects")
        void shouldHaveSameHashCodeForEqualObjects() {
            Book book1 = new Book("Test Book", "Test Author");
            Book book2 = new Book("Test Book", "Test Author");
            
            book1.setId(1L);
            book2.setId(1L);
            
            assertEquals(book1.hashCode(), book2.hashCode());
        }

        @Test
        @DisplayName("Should have different hash codes for different objects")
        void shouldHaveDifferentHashCodesForDifferentObjects() {
            Book book1 = new Book("Test Book 1", "Test Author");
            Book book2 = new Book("Test Book 2", "Test Author");
            
            book1.setId(1L);
            book2.setId(2L);
            
            assertNotEquals(book1.hashCode(), book2.hashCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings(String input) {
            book.setTitle(input);
            book.setAuthor(input);
            
            assertEquals(input, book.getTitle());
            assertEquals(input, book.getAuthor());
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "Very Long Title That Exceeds Normal Length But Should Still Be Handled Properly By The System"})
        @DisplayName("Should handle various string lengths")
        void shouldHandleVariousStringLengths(String input) {
            book.setTitle(input);
            assertEquals(input, book.getTitle());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            String titleWithSpecialChars = "Test Book with @#$%^&*() characters";
            String authorWithSpecialChars = "Author with émojis 🚀 and symbols";
            
            book.setTitle(titleWithSpecialChars);
            book.setAuthor(authorWithSpecialChars);
            
            assertEquals(titleWithSpecialChars, book.getTitle());
            assertEquals(authorWithSpecialChars, book.getAuthor());
        }
    }
}
