package com.reciklaza.libraryproject.repository;

import com.reciklaza.libraryproject.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.ISBN = :ISBN")
    Optional<Book> findByISBN(@Param("ISBN") String isbn);

    @SuppressWarnings("unused")
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") String id);

    @Query("SELECT b FROM Book b WHERE b.available = :available")
    List<Book> findByAvailable(@Param("available") boolean available);
}
