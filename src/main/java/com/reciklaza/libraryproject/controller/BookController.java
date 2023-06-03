package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.repository.BookRepository;
import com.reciklaza.libraryproject.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;


    @GetMapping(path = "/public/books")
    public ResponseEntity<List<Book>> getAllBooks() throws Exception {
        return ResponseEntity.ok().body(bookService.getAll());
    }

    @GetMapping(path = "/public/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(bookService.getById(id));
    }

    @PostMapping(path = "admin/books")
    public ResponseEntity<Book> postBook(@RequestBody Book book) {
        return ResponseEntity.ok().body(bookRepository.save(book));
    }

}
