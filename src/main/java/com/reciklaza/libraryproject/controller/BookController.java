package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDTO;
import com.reciklaza.libraryproject.repository.AuthorRepository;
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
    private final AuthorRepository authorRepository;


    @GetMapping(path = "/public/books")
    public ResponseEntity<List<BookDTO>> getAllBooks(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "lastname", required = false) String lastname) {
        if (name != null && lastname != null) {
            return ResponseEntity.ok().body(bookService.getByAuthor(name, lastname));
        }
        return ResponseEntity.ok().body(bookService.getAll());
    }

    @GetMapping(path = "/public/books/{isbn}")
    public ResponseEntity<BookDTO> getBookByISBN(@PathVariable(value = "isbn") String isbn) {
        return ResponseEntity.ok().body(bookService.getByISBN(isbn));
    }


    @PostMapping(path = "admin/books")
    public ResponseEntity<BookDTO> postBook(@RequestBody Book book) {
        return ResponseEntity.ok().body(bookService.save(book));
    }

}
