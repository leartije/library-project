package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.entity.user.User;
import com.reciklaza.libraryproject.exception.BookNotAvailableException;
import com.reciklaza.libraryproject.exception.BookNotFoundException;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import com.reciklaza.libraryproject.repository.AuthorRepository;
import com.reciklaza.libraryproject.repository.BookRepository;
import com.reciklaza.libraryproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.reciklaza.libraryproject.util.Util.validateNotBlank;

/**
 - Service class that provides business logic for managing books in the library.
 - This service class interacts with the repositories for books, authors, and users to perform various operations
 such as retrieving books, adding books, deleting books, borrowing books, and returning books.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;

    /**
     Retrieves a list of all books.
     @return List of books.
     @throws BookNotFoundException If no books are found in the database.
     */
    public List<BookDto> getAll() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books in database");
        }
        return booksListToBookDTOList(books);
    }

    /**
     Retrieves a book by its ID.
     @param id The ID of the book.
     @return The book with the specified ID.
     @throws BookNotFoundException If no book is found with the given ID.
     */
    @SuppressWarnings("unused")
    public BookDto getById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id=%d is not found.", id));
        }
        return bookToBookDTO(book.get());
    }

    /**
     Retrieves a book by its ISBN.
     @param isbn The ISBN of the book.
     @return The book with the specified ISBN.
     @throws BookNotFoundException If no book is found with the given ISBN.
     */
    public BookDto getByISBN(String isbn) {
        Optional<Book> book = bookRepository.findByISBN(isbn);
        if (book.isPresent()) {
            return bookToBookDTO(book.get());
        }
        throw new BookNotFoundException(String.format("Book with isbn=%s is not found.", isbn));
    }

    /**
     Retrieves a list of books by the author's name and lastname.
     @param firstName The firstname of the author.
     @param lastname The lastname of the author.
     @return List of books written by the specified author.
     @throws BookNotFoundException If no books are found by the specified author.
     */
    public List<BookDto> getByAuthor(String firstName, String lastname) {
        Optional<Author> authorByNameAndLastname =
                authorRepository.findByNameIgnoreCaseAndLastnameIgnoreCase(firstName, lastname);
        if (authorByNameAndLastname.isEmpty()) {
            throw new BookNotFoundException(String.format("There are no books by '%s %s'.", firstName, lastname));
        }
        Author author = authorByNameAndLastname.get();
        return booksListToBookDTOList(author.getBooks());
    }

    /**
     Retrieves a list of books based on availability.
     @param available The availability status of the books (true for available, false for not available).
     @return List of books based on availability.
     @throws BookNotFoundException If no books are found based on the availability.
     */
    public List<BookDto> getByAvailable(boolean available) {
        List<Book> books = bookRepository.findByAvailable(available);
        if (books.isEmpty()) {
            throw new BookNotFoundException(String.format("%s",
                    available ? "No available books in database" : "All books are available"));
        }
        return booksListToBookDTOList(books);
    }

    /**
     Saves a new book to the database.
     @param book The book to be saved.
     @return The saved book.
     @throws NotValidUserSubmissionException If the submitted book is not valid.
     */
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

    /**
     Deletes a book from the database by its ID.
     @param id The ID of the book to be deleted.
     @return The confirmation message for the deleted book.
     @throws BookNotFoundException If no book is found with the given ID.
     */
    public String deleteBookById(Long id) {
        Optional<Book> byId = bookRepository.findById(id);
        if (byId.isPresent()) {
            bookRepository.deleteById(id);
            return String.format("'%s' is deleted from database", byId.get().getTitle());
        }
        throw new BookNotFoundException(String.format("The Book with id = %d is not found", id));
    }

    /**
     Borrows a book for the specified user.
     @param bookId The ID of the book to be borrowed.
     @param user The user who is borrowing the book.
     @return The confirmation message for the borrowed book.
     @throws RuntimeException If the maximum number of books that can be rented is reached.
     @throws BookNotFoundException If no book is found with the given ID.
     @throws BookNotAvailableException If the book is not available for borrowing.
     */
    public String borrowBook(long bookId, User user) {
        if (user.getNumOfBooks() >= 2) {
            throw new RuntimeException("The maximum number of books that can be rented is 2.");
        }

        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent() && book.get().isAvailable()) {
            book.get().setUser(user);
            book.get().setAvailable(false);
            user.setNumOfBooks(user.getNumOfBooks() + 1);
            userRepository.save(user);
            bookRepository.save(book.get());
            return String.format("%s %s is borrowed '%s'", user.getFirstname(), user.getLastname(), book.get().getTitle());
        }

        if (book.isPresent()) {
            throw new BookNotAvailableException(String.format("%s is not available", book.get().getTitle()));
        }
        throw new BookNotFoundException(String.format("The Book with id = %d is not found", bookId));
    }

    /**
     Returns a borrowed book to the library.
     @param bookId The ID of the book to be returned.
     @param user The user who is returning the book.
     @return The confirmation message for returning the book to the library.
     @throws BookNotFoundException If no book is found with the given ID.
     */
    public String returnBook(long bookId, User user) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent() && !book.get().isAvailable() && Objects.equals(user.getId(), book.get().getUser().getId())) {
            book.get().setAvailable(true);
            user.setNumOfBooks(user.getNumOfBooks() - 1);
            bookRepository.save(book.get());
            userRepository.save(user);
            return String.format("You return '%s' to the library", book.get().getTitle());
        }
        if (book.isPresent() && !book.get().isAvailable() && Objects.equals(user.getId(), book.get().getUser().getId())) {
            return String.format("You didn't borrow %s", book.get().getTitle());
        }

        throw new BookNotFoundException(String.format("The Book with id = %d is not found", bookId));
    }

    //private util methods

    /**
     Converts a list of books to a list of book DTOs.
     @param books The list of books to be converted.
     @return The list of book DTOs.
     */
    private List<BookDto> booksListToBookDTOList(List<Book> books) {
        List<BookDto> booksResponse = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDTO = bookToBookDTO(book);
            booksResponse.add(bookDTO);
        }
        return booksResponse;
    }

    /**
     Converts a book to a book DTO.
     @param book The book to be converted.
     @return The book DTO.
     */
    private BookDto bookToBookDTO(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(String.format("%s %s", book.getAuthor().getName(), book.getAuthor().getLastname()))
                .ISBN(book.getISBN())
                .available(book.isAvailable())
                .build();
    }

    /**
     Validates the fields of a book.
     @param book The book to be validated.
     @throws NotValidUserSubmissionException If the book is not valid.
     */
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
