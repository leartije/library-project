package com.reciklaza.libraryproject.service;

import com.reciklaza.libraryproject.entity.Author;
import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.repository.AuthorRepository;
import com.reciklaza.libraryproject.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private BookService underTest;

    @Test
    public void getAllBooks() {
        Author author = new Author(null, "Author", "Name", null);
        Book book1 = new Book(null, "Title1", author, "12345", true, null);
        Book book2 = new Book(null, "Title2", author, "54321", true, null);
        List<Book> books = List.of(book1, book2);

        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> all = underTest.getAll();

        assertEquals(2, all.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    public void testSaveBook() {
        Author author = new Author(null, "Author", "Name", null);
        Book book1 = new Book(null, "Title1", author, "12345", true, null);

        when(authorRepository.save(author)).thenReturn(author);
        when(bookRepository.save(book1)).thenReturn(book1);

        BookDto save = underTest.save(book1);

        assertEquals(book1.getTitle(), save.getTitle());
    }


}