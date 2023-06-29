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
import com.reciklaza.libraryproject.util.BookServiceUtilMethods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private final static BookDto BOOK_1_DTO = new BookDto(BOOK_1.getId(), BOOK_1.getTitle(),
            String.format("%s %s", BOOK_1.getAuthor().getName(), BOOK_1.getAuthor().getLastname()), BOOK_1.getISBN(), BOOK_1.isAvailable());
    private final static BookDto BOOK_2_DTO = new BookDto(BOOK_2.getId(), BOOK_2.getTitle(),
            String.format("%s %s", BOOK_2.getAuthor().getName(), BOOK_2.getAuthor().getLastname()), BOOK_2.getISBN(), BOOK_2.isAvailable());
    private final static BookDto BOOK_3_DTO = new BookDto(BOOK_3.getId(), BOOK_3.getTitle(),
            String.format("%s %s", BOOK_3.getAuthor().getName(), BOOK_3.getAuthor().getLastname()), BOOK_3.getISBN(), BOOK_3.isAvailable());

    private final static List<BookDto> BOOKS_DTO = List.of(BOOK_1_DTO, BOOK_2_DTO, BOOK_3_DTO);

    private final static String ISBN = "9781234567890";

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookServiceUtilMethods bookServiceUtilMethods;
    @InjectMocks
    private BookService underTest;


    @Test
    public void testGetAllBooks() {
        when(bookServiceUtilMethods.transferToPage(bookServiceUtilMethods.booksListToBookDTOList(BOOKS), 0, 10))
                .thenReturn(new PageImpl<>(BOOKS_DTO));
        when(bookRepository.findAll()).thenReturn(BOOKS);

        Page<BookDto> response = underTest.getAll(0, 10);

        assertEquals(3, response.getTotalElements());
        verify(bookRepository, times(1)).findAll();
    }


    @Test
    public void testGetById_WhenIdIsValid() {
        when(bookServiceUtilMethods.bookToBookDTO(BOOK_2)).thenReturn(BOOK_2_DTO);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(BOOK_2));

        BookDto response = underTest.getById(2L);

        assertEquals("Naslov Knjige 2", response.getTitle());
        assertEquals("Maja Majic", response.getAuthor());
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

        when(bookServiceUtilMethods.bookToBookDTO(BOOK_1)).thenReturn(BOOK_1_DTO);
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(BOOK_1));

        BookDto response = underTest.getByISBN(ISBN);

        assertEquals(ISBN, response.getISBN());
        assertEquals("Naslov Knjige 1", response.getTitle());
        assertEquals("Marko Markovic", response.getAuthor());
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

        when(bookServiceUtilMethods.transferToPage(bookServiceUtilMethods.booksListToBookDTOList(BOOKS), 0, 10))
                .thenReturn(new PageImpl<>(List.of(BOOK_1_DTO, BOOK_3_DTO)));

        when(authorRepository.findByNameIgnoreCaseAndLastnameIgnoreCase(AUTHOR_1.getName(), AUTHOR_1.getLastname()))
                .thenReturn(Optional.of(AUTHOR_1));

        Page<BookDto> response = underTest.getByAuthor(AUTHOR_1.getName(), AUTHOR_1.getLastname(), 0, 10);

        assertEquals(2, response.getTotalElements());
        verify(authorRepository, times(1))
                .findByNameIgnoreCaseAndLastnameIgnoreCase(AUTHOR_1.getName(), AUTHOR_1.getLastname());
    }


    @Test
    void getByAuthor_WhenAuthorNameIsNotValid_ShouldThrowBookNotFoundException() {
        BookNotFoundException response = assertThrows(BookNotFoundException.class, () -> underTest.getByAuthor(AUTHOR_1.getName(), AUTHOR_1.getLastname(), 0, 10));

        assertEquals(String.format("There are no books by '%s %s'.", AUTHOR_1.getName(), AUTHOR_1.getLastname()), response.getLocalizedMessage());
    }

    @Test
    void getByAvailable() {
        when(bookServiceUtilMethods.transferToPage(bookServiceUtilMethods.booksListToBookDTOList(BOOKS), 0, 10))
                .thenReturn(new PageImpl<>(List.of(BOOK_1_DTO, BOOK_2_DTO)));

        when(bookRepository.findByAvailable(true)).thenReturn(List.of(BOOK_1, BOOK_3));

        Page<BookDto> response = underTest.getByAvailable(true, 0, 10);

        assertEquals(2, response.getTotalElements());
        verify(bookRepository, times(1)).findByAvailable(true);
    }


    @Test
    public void testSaveBook_WithValidBookFields() {
        when(bookServiceUtilMethods.bookToBookDTO(BOOK_1)).thenReturn(BOOK_1_DTO);

        BookDto response = underTest.save(BOOK_1);

        assertEquals("Naslov Knjige 1", response.getTitle());
        assertEquals("12345", response.getISBN());
        verify(bookRepository, times(1)).save(BOOK_1);
        verify(authorRepository, times(1)).save(AUTHOR_1);
    }

    @Test
    public void testSaveBook_WhenISBNAlreadyExist() {
        when(bookRepository.findByISBN("34557")).thenReturn(Optional.of(BOOK_3));

        NotValidUserSubmissionException response =
                assertThrows(NotValidUserSubmissionException.class, () -> underTest.save(BOOK_3));

        assertEquals("'34557' ISBN already exist", response.getLocalizedMessage());
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