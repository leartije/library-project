package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import com.reciklaza.libraryproject.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping(path = "/public/books")
    public ResponseEntity<List<BookDto>> getAllBooks(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "lastname", required = false) String lastname,
            @RequestParam(name = "available", required = false) Boolean available) {
        if (name != null && lastname != null) {
            return ResponseEntity.ok().body(bookService.getByAuthor(name, lastname));
        }
        if (available != null) {
            return ResponseEntity.ok().body(bookService.getByAvailable(available));
        }
        return ResponseEntity.ok().body(bookService.getAll());
    }

    @GetMapping(path = "/public/books/{isbn}")
    public ResponseEntity<BookDto> getBookByISBN(@PathVariable(value = "isbn") String isbn) {
        return ResponseEntity.ok().body(bookService.getByISBN(isbn));
    }

    @PostMapping(path = "/admin/books")
    public ResponseEntity<BookDto> postBook(@RequestBody Book book) {
        if (book == null) {
            throw new NotValidUserSubmissionException("Book is null");
        }

        return ResponseEntity.ok().body(bookService.save(book));
    }

    @DeleteMapping(path = "/admin/book/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable(value = "id") Long id) {
        if (id == null) {
            throw new NotValidUserSubmissionException("Id is null!");
        }
        return ResponseEntity.ok(bookService.deleteBookById(id));
    }

}
