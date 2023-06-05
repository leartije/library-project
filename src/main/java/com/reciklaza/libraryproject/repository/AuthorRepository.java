package com.reciklaza.libraryproject.repository;

import com.reciklaza.libraryproject.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) = LOWER(:name) AND LOWER(a.lastname) = LOWER(:lastname)")
    Optional<Author> findByNameIgnoreCaseAndLastnameIgnoreCase(@Param("name") String name, @Param("lastname") String lastname);


}
