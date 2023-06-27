package com.reciklaza.libraryproject.controller;

import com.reciklaza.libraryproject.config.JwtService;
import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.entity.user.Role;
import com.reciklaza.libraryproject.entity.user.User;
import com.reciklaza.libraryproject.exception.UnauthorisedAccessException;
import com.reciklaza.libraryproject.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private final static List<BookDto> BOOKS = List.of(
            new BookDto(null, "Naslov1", "Pisac Prvi", "12345", true),
            new BookDto(null, "Naslov2", "Pisac Drugi", "123456", true),
            new BookDto(null, "Naslov3", "Pisac Treci", "1234567", true)
    );

    private final static BookDto BOOK_DTO = new BookDto(null, "Naslov Knjige", "Marko Markovic", "12345", true);
    private final static Author AUTHOR = new Author(1L, "Marko", "Markovic", null);
    private final static User ADMIN = new User(1L, "Janko", "Jankovic", "janko@email.com", "54321", Role.ADMIN, 0);
    private final static User USER = new User(2L, "Stanko", "Stankovic", "stanko@email.com", "54321", Role.USER, 0);
    private final static Book BOOK = new Book(1L, "Naslov Knjige", AUTHOR, "12345", true, USER);
    private final static String MOCK_JWT = "mockJWToken";

    @Mock
    private BookService bookService;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private BookController underTest;

    @Test
    void testFetAllBooks_Without_Filter() {
        when(bookService.getAll()).thenReturn(BOOKS);

        ResponseEntity<List<BookDto>> allBooks = underTest.getAllBooks(null, null, null);

        assertEquals(HttpStatus.OK, allBooks.getStatusCode());
        assertEquals(3, Objects.requireNonNull(allBooks.getBody()).size());
    }

    @Test
    void testFetAllBooks_ByAvailable() {
        when(bookService.getByAvailable(true)).thenReturn(BOOKS);

        ResponseEntity<List<BookDto>> allBooks = underTest.getAllBooks(null, null, true);

        assertEquals(HttpStatus.OK, allBooks.getStatusCode());
        assertEquals(BOOKS, allBooks.getBody());
        assertEquals("Naslov2", Objects.requireNonNull(allBooks.getBody()).get(1).getTitle());
    }

    @Test
    void testGetBookByISBN_When_ISBN_Is_Valid() {
        String isbn = "12345";

        when(bookService.getByISBN(isbn)).thenReturn(BOOK_DTO);

        ResponseEntity<BookDto> responseEntity = underTest.getBookByISBN(isbn);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(BOOK_DTO, responseEntity.getBody());
    }

    @Test
    void testPostBook_WithAdminPrivileges() {
        when(bookService.save(BOOK)).thenReturn(BOOK_DTO);
        when(jwtService.authorised(MOCK_JWT, Role.ADMIN)).thenReturn(ADMIN);

        ResponseEntity<BookDto> response = underTest.postBook(MOCK_JWT, BOOK);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(BOOK_DTO, response.getBody());
        assertEquals("Marko Markovic", Objects.requireNonNull(response.getBody()).getAuthor());
    }

    @Test
    void testPostBook_WithoutAdminPrivileges() {
        assertThrows(UnauthorisedAccessException.class, () -> underTest.postBook(MOCK_JWT, BOOK));
    }

    @Test
    void testDeleteBookById_WithAdminPrivileges() {
        String title = "Nova knjiga";

        when(bookService.deleteBookById(1L)).thenReturn(String.format("'%s' is deleted from database", title));
        when(jwtService.authorised(MOCK_JWT, Role.ADMIN)).thenReturn(ADMIN);

        ResponseEntity<String> response = underTest.deleteBookById(MOCK_JWT, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("'Nova knjiga' is deleted from database", response.getBody());
    }

    @Test
    void testDeleteBookById_WithoutAdminPrivileges() {
        assertThrows(UnauthorisedAccessException.class, () -> underTest.deleteBookById(MOCK_JWT, 1L));
    }

    @Test
    void testBorrowTheBook_WithAppropriatePrivileges() {
        String firstName = USER.getFirstname();
        String lastName = USER.getLastname();
        String title = BOOK.getTitle();

        when(bookService.borrowBook(1L, USER)).thenReturn(String.format("%s %s is borrowed %s", firstName, lastName, title));
        when(jwtService.authorised(MOCK_JWT, Role.USER)).thenReturn(USER);

        ResponseEntity<String> response = underTest.borrowTheBook(MOCK_JWT, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stanko Stankovic is borrowed Naslov Knjige", response.getBody());
    }

    @Test
    void testBorrowTheBook_WithoutAppropriatePrivileges() {
        assertThrows(UnauthorisedAccessException.class, () -> underTest.borrowTheBook(MOCK_JWT, 1L));
    }

    @Test
    void testReturnTheBook_WithAppropriatePrivileges() {
        String title = BOOK.getTitle();

        when(bookService.returnBook(1L, USER)).thenReturn(String.format("You return '%s' to the library", title));
        when(jwtService.authorised(MOCK_JWT, Role.USER)).thenReturn(USER);

        ResponseEntity<String> response = underTest.returnTheBook(MOCK_JWT, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You return 'Naslov Knjige' to the library", response.getBody());
    }

    @Test
    void testReturnTheBook_WithoutAppropriatePrivileges() {
        assertThrows(UnauthorisedAccessException.class, () -> underTest.returnTheBook(MOCK_JWT, 1L));
    }


}