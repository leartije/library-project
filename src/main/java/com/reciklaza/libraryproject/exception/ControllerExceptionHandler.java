package com.reciklaza.libraryproject.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {BookNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleBookNotFoundException(HttpServletRequest request, BookNotFoundException ex) {
        String error = "Book not found: " + request.getRequestURI();
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, LocalDateTime.now(), error, ex.getMessage());
        return buildResponseEntity(errorMessage);
    }

    @ExceptionHandler(value = {BookNotAvailableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNotAvailableBookException(HttpServletRequest request, BookNotAvailableException ex) {
        String error = "The Book is not available: " + request.getRequestURI();
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, LocalDateTime.now(), error, ex.getMessage());
        return buildResponseEntity(errorMessage);

    }

    @ExceptionHandler(value = {NotValidUserSubmissionException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNotValidBookSubmissionException(HttpServletRequest request, NotValidUserSubmissionException ex) {
        String error = "Unable to submit the book: " + request.getRequestURI();
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, LocalDateTime.now(), error, ex.getMessage());
        return buildResponseEntity(errorMessage);
    }

    @ExceptionHandler(value = {UnauthorisedAccessException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnauthorisedAccessException(HttpServletRequest request, UnauthorisedAccessException ex) {
        String error = "Unauthorised access denied" + request.getRequestURI();
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, LocalDateTime.now(), error, ex.getMessage());
        return buildResponseEntity(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> globalExceptionHandler(HttpServletRequest request, Exception ex) {
        String error = "An unexpected error occurred while invoking the endpoint: " + request.getRequestURI();
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), error, ex.getMessage());
        return buildResponseEntity(errorMessage);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorMessage errorMessage) {
        return new ResponseEntity<>(errorMessage, errorMessage.getStatus());
    }

}


