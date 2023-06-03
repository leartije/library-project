package com.reciklaza.libraryproject.repository;

import com.reciklaza.libraryproject.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.ISBN = :ISBN")
    Optional<Book> findByISBN(@Param("ISBN") String isbn);

    @Query("SELECT b FROM Book b WHERE b.author.id = :id")
    List<Book> findByAuthorId(@Param("id") String id);

}
