package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.exception.BookNotFoundException;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import com.reciklaza.libraryproject.repository.AuthorRepository;
import com.reciklaza.libraryproject.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reciklaza.libraryproject.util.Util.validateNotBlank;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public List<BookDto> getAll() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books in database");
        }
        return booksListToBookDTOList(books);
    }

    public BookDto getById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id=%d is not found.", id));
        }
        return bookToBookDTO(book.get());
    }

    public BookDto getByISBN(String isbn) {
        Optional<Book> book = bookRepository.findByISBN(isbn);
        if (book.isPresent()) {
            return bookToBookDTO(book.get());
        }
        throw new BookNotFoundException(String.format("Book with isbn=%s is not found.", isbn));
    }

    public List<BookDto> getByAuthor(String name, String lastname) {
        Optional<Author> authorByNameAndLastname =
                authorRepository.findByNameIgnoreCaseAndLastnameIgnoreCase(name, lastname);
        if (authorByNameAndLastname.isEmpty()) {
            throw new BookNotFoundException(String.format("There are no books by '%s %s'.", name, lastname));
        }
        Author author = authorByNameAndLastname.get();
        return booksListToBookDTOList(author.getBooks());
    }

    public List<BookDto> getByAvailable(boolean available) {
        List<Book> books = bookRepository.findByAvailable(available);
        if (books.isEmpty()) {
            throw new BookNotFoundException(String.format("%s",
                    available ? "No available books in database" : "All books are available"));
        }
        return booksListToBookDTOList(books);
    }

    public BookDto save(Book book) {
        validateBook(book);

        Optional<Author> authorByNameAndLastname = authorRepository
                .findByNameIgnoreCaseAndLastnameIgnoreCase(book.getAuthor().getName(), book.getAuthor().getLastname());

        Optional<Book> bookByIsbn = bookRepository.findByISBN(book.getISBN());
        if (bookByIsbn.isPresent()) {
            throw new NotValidUserSubmissionException(String.format("'%s' ISBN already exist", bookByIsbn.get().getISBN()));
        }

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

    public String deleteBookById(Long id) {
        Optional<Book> byId = bookRepository.findById(id);
        if (byId.isPresent()) {
            bookRepository.deleteById(id);
            return String.format("'%s' is deleted from database", byId.get().getTitle());
        }
        throw new BookNotFoundException(String.format("The Book with id = %d is not found", id));
    }

    private List<BookDto> booksListToBookDTOList(List<Book> books) {
        List<BookDto> booksResponse = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDTO = bookToBookDTO(book);
            booksResponse.add(bookDTO);
        }
        return booksResponse;
    }

    private BookDto bookToBookDTO(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(String.format("%s %s", book.getAuthor().getName(), book.getAuthor().getLastname()))
                .ISBN(book.getISBN())
                .available(book.isAvailable())
                .build();
    }

    private void validateBook(Book book) {
        validateNotBlank(book.getTitle(), "Title");
        if (book.getAuthor() == null) {
            throw new NotValidUserSubmissionException("Author field can't be blank");
        }
        validateNotBlank(book.getAuthor().getName(), "Authors firstname");
        validateNotBlank(book.getAuthor().getLastname(), "Authors lastname");
        validateNotBlank(book.getISBN(), "ISBN");
    }
}
