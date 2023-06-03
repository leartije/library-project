package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.exception.BookNotFoundException;
import com.reciklaza.libraryproject.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getAll() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books in database");
        }
        return books;
    }

    public Book getById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id=%d is not found", id));
        }
        return book.get();
    }

}
