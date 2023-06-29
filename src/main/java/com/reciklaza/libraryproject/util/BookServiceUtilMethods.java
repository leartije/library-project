package com.reciklaza.libraryproject.util;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.entity.dto.BookDto;
import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.reciklaza.libraryproject.util.Util.*;
@Component
public class BookServiceUtilMethods {


    /**
     Converts a list of books to a list of book DTOs.
     @param books The list of books to be converted.
     @return The list of book DTOs.
     */
    public List<BookDto> booksListToBookDTOList(List<Book> books) {
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
    public BookDto bookToBookDTO(Book book) {
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
    public void validateBook(Book book) {
        validateNotBlank(book.getTitle(), "Title");
        if (book.getAuthor() == null) {
            throw new NotValidUserSubmissionException("Author field can't be blank");
        }
        validateNotBlank(book.getAuthor().getName(), "Authors firstname");
        validateNotBlank(book.getAuthor().getLastname(), "Authors lastname");
        validateNotBlank(book.getISBN(), "ISBN");
    }

    /**
     * Transfers a list of BookDto objects to a paginated Page of BookDto objects.
     *
     * @param bookDtoList The list of BookDto objects.
     * @param number      The page number (zero-based index).
     * @param size        The page size.
     * @return The paginated Page of BookDto objects.
     */
    public Page<BookDto> transferToPage(List<BookDto> bookDtoList, int number, int size) {
        int startIndex = number * size;
        int endIndex = Math.min(startIndex + size, bookDtoList.size());

        List<BookDto> pageContent = bookDtoList.subList(startIndex, endIndex);
        PageRequest pageRequest = PageRequest.of(number, size);
        long totalElements = bookDtoList.size();

        return new PageImpl<>(pageContent, pageRequest, totalElements);
    }
}
