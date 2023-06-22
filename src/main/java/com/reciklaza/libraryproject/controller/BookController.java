package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.config.JwtService;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.entity.user.Role;
import com.reciklaza.libraryproject.entity.user.User;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import com.reciklaza.libraryproject.exception.UnauthorisedAccessException;
import com.reciklaza.libraryproject.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling book-related API endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private static final String AUTHORIZATION = "Authorization";
    private final BookService bookService;
    private final JwtService jwtService;

    /**
     * Retrieves a list of all books.
     *
     * @param name      Optional parameter to filter books by author's name.
     * @param lastname  Optional parameter to filter books by author's lastname.
     * @param available Optional parameter to filter books by availability.
     * @return ResponseEntity containing the list of book DTOs.
     */
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

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return ResponseEntity containing the book DTO.
     */
    @GetMapping(path = "/public/books/{isbn}")
    public ResponseEntity<BookDto> getBookByISBN(@PathVariable(value = "isbn") String isbn) {
        return ResponseEntity.ok().body(bookService.getByISBN(isbn));
    }

    /**
     * Adds a new book to the database.
     *
     * @param jwt  The JWT token for authorization.
     * @param book The book entity to be added.
     * @return ResponseEntity containing the added book DTO.
     * @throws UnauthorisedAccessException if the user is not authorized.
     */
    @PostMapping(path = "/admin/books")
    public ResponseEntity<BookDto> postBook(
            @RequestHeader(AUTHORIZATION) String jwt,
            @RequestBody Book book
    ) {
        if (jwtService.authorised(jwt, Role.ADMIN) != null) {
            if (book == null) {
                throw new NotValidUserSubmissionException("Book is null");
            }

            return ResponseEntity.ok().body(bookService.save(book));
        }
        throw new UnauthorisedAccessException("To add the book, you need to have administrator privileges");
    }

    /**
     * Deletes a book from the database by its ID.
     *
     * @param jwt The JWT token for authorization.
     * @param id  The ID of the book to be deleted.
     * @return ResponseEntity containing a message indicating the deletion status.
     * @throws UnauthorisedAccessException if the user is not authorized.
     */
    @DeleteMapping(path = "/admin/book/{id}")
    public ResponseEntity<String> deleteBookById(
            @RequestHeader(AUTHORIZATION) String jwt,
            @PathVariable(value = "id") Long id
    ) {
        if (jwtService.authorised(jwt, Role.ADMIN) != null) {
            if (id == null) {
                throw new NotValidUserSubmissionException("Id is null!");
            }
            return ResponseEntity.ok(bookService.deleteBookById(id));
        }
        throw new UnauthorisedAccessException("To delete the book, you need to have administrator privileges");
    }

    /**
     * Borrows a book by its ID for the logged-in user.
     *
     * @param jwt The JWT token for authorization.
     * @param id  The ID of the book to be borrowed.
     * @return ResponseEntity containing a message indicating the borrowing status.
     * @throws UnauthorisedAccessException if the user is not logged in.
     */
    @GetMapping(path = "/user/book/borrow/{id}")
    public ResponseEntity<String> borrowTheBook(
            @RequestHeader(AUTHORIZATION) String jwt,
            @PathVariable(value = "id") Long id
    ) {
        User authorised = jwtService.authorised(jwt, Role.USER);
        if (authorised != null) {
            return ResponseEntity.ok(bookService.borrowBook(id, authorised));

        }
        throw new UnauthorisedAccessException("To borrow the book, a user must be logged in.");
    }

    /**
     * Returns a borrowed book by its ID for the logged-in user.
     *
     * @param jwt The JWT token for authorization.
     * @param id  The ID of the book to be returned.
     * @return ResponseEntity containing a message indicating the return status.
     * @throws UnauthorisedAccessException if the user is not logged in.
     */
    @GetMapping(path = "/user/book/return/{id}")
    public ResponseEntity<String> returnTheBook(
            @RequestHeader(AUTHORIZATION) String jwt,
            @PathVariable(value = "id") Long id
    ) {
        User authorised = jwtService.authorised(jwt, Role.USER);
        if (authorised != null) {
            return ResponseEntity.ok(bookService.returnBook(id, authorised));

        }
        throw new UnauthorisedAccessException("To return the book, a user must be logged in.");
    }


}
