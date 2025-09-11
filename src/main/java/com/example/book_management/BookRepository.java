package com.example.book_management;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find books by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find books by author (case insensitive)
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Find books by title or author (case insensitive)
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> findByTitleOrAuthorContaining(@Param("keyword") String keyword);
}
