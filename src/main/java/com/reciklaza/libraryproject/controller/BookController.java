package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Book> postBook(@RequestBody Book book) {
        return ResponseEntity.ok().body(bookRepository.save(book));
    }

}
