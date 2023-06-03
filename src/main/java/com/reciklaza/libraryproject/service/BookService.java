package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDTO;
import com.reciklaza.libraryproject.exception.BookNotFoundException;
import com.reciklaza.libraryproject.repository.AuthorRepository;
import com.reciklaza.libraryproject.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public List<BookDTO> getAll() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books in database");
        }
        return booksListToBookDTOList(books);
    }

    public BookDTO getById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id=%d is not found", id));
        }
        return bookToBookDTO(book.get());
    }

    public BookDTO getByISBN(String isbn) {
        Optional<Book> book = bookRepository.findByISBN(isbn);
        if (book.isPresent()) {
            return bookToBookDTO(book.get());
        }
        throw new BookNotFoundException("Book with ISBN = " + isbn + " is not found");
    }

    public List<BookDTO> getByAuthor(String name, String lastname) {
        Optional<Author> authorByNameAndLastname = authorRepository.findByNameAndLastname(name, lastname);
        if (authorByNameAndLastname.isEmpty()) {
            throw new BookNotFoundException("there is not books by this author");
        }
        Author author = authorByNameAndLastname.get();
        return booksListToBookDTOList(author.getBooks());
    }

    public BookDTO save(Book book) {
        Optional<Author> authorByNameAndLastname = authorRepository
                .findByNameAndLastname(book.getAuthor().getName(), book.getAuthor().getLastname());

        if (authorByNameAndLastname.isEmpty()) {
            Author author = Author.builder()
                    .name(book.getAuthor().getName())
                    .lastname(book.getAuthor().getLastname())
                    .build();
            Author save = authorRepository.save(author);
            book.setAuthor(save);
        } else {
            Author existingAuthor = authorByNameAndLastname.get();
            book.setAuthor(existingAuthor);
        }

        bookRepository.save(book);
        return bookToBookDTO(book);
    }

    private List<BookDTO> booksListToBookDTOList(List<Book> books) {
        List<BookDTO> booksResponse = new ArrayList<>();
        for (Book b : books) {
            BookDTO bookDTO = bookToBookDTO(b);
            booksResponse.add(bookDTO);
        }
        return booksResponse;
    }

    private BookDTO bookToBookDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(String.format("%s %s", book.getAuthor().getName(), book.getAuthor().getLastname()))
                .ISBN(book.getISBN())
                .available(book.isAvailable())
                .build();
    }

}
