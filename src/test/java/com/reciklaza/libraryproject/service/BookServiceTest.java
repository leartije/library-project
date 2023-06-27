package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.entity.user.Role;
import com.reciklaza.libraryproject.entity.user.User;
import com.reciklaza.libraryproject.exception.BookNotFoundException;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import com.reciklaza.libraryproject.repository.AuthorRepository;
import com.reciklaza.libraryproject.repository.BookRepository;
import com.reciklaza.libraryproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private final static Author AUTHOR_1 = new Author(null, "Marko", "Markovic", null);
    private final static Author AUTHOR_2 = new Author(null, "Maja", "Majic", null);
    private final static User USER_1 = new User(1L, "Stanko", "Stankovic", "stanko@email.com", "54321", Role.USER, 0);
    private final static User USER_2 = new User(2L, "Sanja", "Sanjic", "sanja@email.com", "54321", Role.USER, 1);
    private final static Book BOOK_1 = new Book(1L, "Naslov Knjige 1", AUTHOR_1, "12345", true, null);
    private final static Book BOOK_2 = new Book(2L, "Naslov Knjige 2", AUTHOR_2, "23456", false, USER_2);
    private final static Book BOOK_3 = new Book(3L, "Naslov Knjige 3", AUTHOR_1, "34557", true, null);
    private final static List<Book> BOOKS = List.of(BOOK_1, BOOK_2, BOOK_3);
    private final static String ISBN = "9781234567890";

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookService underTest;


    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(BOOKS);

        List<BookDto> response = underTest.getAll();

        assertEquals(3, response.size());
        verify(bookRepository, times(1)).findAll();
    }


    @Test
    public void testGetById_WhenIdIsValid() {
        when(bookRepository.findById(2L)).thenReturn(Optional.of(BOOK_2));

        BookDto response = underTest.getById(2L);

        assertEquals("Naslov Knjige 2", response.getTitle());
        assertEquals(AUTHOR_2.getName() + " " + AUTHOR_2.getLastname(), response.getAuthor());
        verify(bookRepository, times(1)).findById(2L);
    }

    @Test
    public void testGetById_WhenIdIsNotValid_ShouldThrowBookNotFoundException() {
        BookNotFoundException response = assertThrows(BookNotFoundException.class, () -> underTest.getById(1L));

        assertEquals("Book with id=1 is not found.", response.getLocalizedMessage());
    }

    @Test
    public void testGetByISBN() {
        final String ISBN = BOOK_1.getISBN();

        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(BOOK_1));

        BookDto response = underTest.getByISBN(ISBN);

        assertEquals(ISBN, response.getISBN());
        assertEquals("Naslov Knjige 1", response.getTitle());
        assertEquals(AUTHOR_1.getName() + " " + AUTHOR_1.getLastname(), response.getAuthor());
        verify(bookRepository, times(1)).findByISBN(ISBN);
    }

    @Test
    public void testGetByISBN_WithNonExistingISBN_ShouldThrowBookNotFoundException() {
        BookNotFoundException response = assertThrows(BookNotFoundException.class, () -> underTest.getByISBN(ISBN));

        assertEquals("Book with isbn=9781234567890 is not found.", response.getLocalizedMessage());
        verify(bookRepository, times(1)).findByISBN(ISBN);
    }

    @Test
    void getByAuthor_WhenAuthorNameIsValid() {
        AUTHOR_1.setBooks(List.of(BOOK_1, BOOK_3));

        when(authorRepository.findByNameIgnoreCaseAndLastnameIgnoreCase(AUTHOR_1.getName(), AUTHOR_1.getLastname()))
                .thenReturn(Optional.of(AUTHOR_1));

        List<BookDto> response = underTest.getByAuthor(AUTHOR_1.getName(), AUTHOR_1.getLastname());

        assertEquals(2, response.size());
        assertEquals("Naslov Knjige 3", response.get(1).getTitle());
        assertEquals("12345", response.get(0).getISBN());
        assertEquals(AUTHOR_1.getName() + " " + AUTHOR_1.getLastname(), response.get(0).getAuthor());
        verify(authorRepository, times(1))
                .findByNameIgnoreCaseAndLastnameIgnoreCase(AUTHOR_1.getName(), AUTHOR_1.getLastname());
    }

    @Test
    void getByAuthor_WhenAuthorNameIsNotValid_ShouldThrowBookNotFoundException() {
        BookNotFoundException response = assertThrows(BookNotFoundException.class, () -> underTest.getByAuthor(AUTHOR_1.getName(), AUTHOR_1.getLastname()));

        assertEquals(String.format("There are no books by '%s %s'.", AUTHOR_1.getName(), AUTHOR_1.getLastname()), response.getLocalizedMessage());
    }

    @Test
    void getByAvailable() {
        when(bookRepository.findByAvailable(true)).thenReturn(List.of(BOOK_1, BOOK_3));

        List<BookDto> response = underTest.getByAvailable(true);

        assertEquals(2, response.size());
        assertTrue(response.get(1).isAvailable());
        verify(bookRepository, times(1)).findByAvailable(true);
    }

    @Test
    public void testSaveBook_WithValidBookFields() {
        when(bookRepository.save(BOOK_1)).thenReturn(BOOK_1);
        when(authorRepository.save(AUTHOR_1)).thenReturn(AUTHOR_1);

        BookDto response = underTest.save(BOOK_1);

        assertEquals("Naslov Knjige 1", response.getTitle());
        assertEquals("12345", response.getISBN());
        verify(bookRepository, times(1)).save(BOOK_1);
        verify(authorRepository, times(1)).save(AUTHOR_1);
    }

    @Test
    public void testSaveBook_WhenAuthorFieldIsNull_ShouldThrowNotValidUserSubmissionException() {
        Book book = new Book(1L, "Naslov", null, "1234", true, null);
        NotValidUserSubmissionException response = assertThrows(NotValidUserSubmissionException.class, () -> underTest.save(book));

        assertEquals("Author field can't be blank", response.getLocalizedMessage());
        assertEquals(NotValidUserSubmissionException.class, response.getClass());
    }

    @Test
    void deleteBookById_WhenIdIsValid() {
        String expected = "'Naslov Knjige 1' is deleted from database";
        when(bookRepository.findById(1L)).thenReturn(Optional.of(BOOK_1));

        String response = underTest.deleteBookById(1L);

        assertEquals(expected, response);
    }

    @Test
    void deleteBookById_WhenIdIsNotValid_ShouldThrowBookNotFoundException() {
        String expected = "The Book with id = 1 is not found";
        BookNotFoundException response = assertThrows(BookNotFoundException.class, () -> underTest.deleteBookById(1L));

        assertEquals(expected, response.getLocalizedMessage());
    }

    @Test
    void testBorrowBook() {
        String expected = "Stanko Stankovic is borrowed 'Naslov Knjige 1'";

        when(bookRepository.findById(1L)).thenReturn(Optional.of(BOOK_1));
        when(userRepository.save(USER_1)).thenReturn(USER_1);

        String response = underTest.borrowBook(BOOK_1.getId(), USER_1);

        assertEquals(expected, response);
        verify(bookRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(USER_1);
    }

    @Test
    void testBorrowBook_WhenUserAlreadyHasBorrowedTwoBooks() {
        String expected = "The maximum number of books that can be rented is 2.";
        User test_user = new User(1L, "Ime", "Prezime", "test@emal.com", "12345", Role.USER, 2);

        RuntimeException response = assertThrows(RuntimeException.class, () -> underTest.borrowBook(1L, test_user));

        assertEquals(expected, response.getLocalizedMessage());
        assertEquals(RuntimeException.class, response.getClass());
    }

    @Test
    void testReturnBook() {
        String expected = "You return 'Naslov Knjige 2' to the library";

        when(bookRepository.findById(2L)).thenReturn(Optional.of(BOOK_2));

        String response = underTest.returnBook(2L, USER_2);
        assertEquals(expected, response);
        assertEquals(0, USER_2.getNumOfBooks());
    }
}