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
    public ResponseEntity<Object> bookNotFoundException(HttpServletRequest request, BookNotFoundException ex) {
        String error = "Error message: " + request.getRequestURI();
        return buildResponseEntity(new ErrorMessage(HttpStatus.NOT_FOUND, LocalDateTime.now(), error, ex.getMessage()));
    }

    @ExceptionHandler(value = {NotValidBookSubmissionException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> notValidBookSubmissionException(HttpServletRequest request, BookNotFoundException ex) {
        String error = "Unable to submit post: " + ex.getMessage();
        return buildResponseEntity(new ErrorMessage(HttpStatus.BAD_REQUEST, LocalDateTime.now(), error, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> globalExceptionHandler(HttpServletRequest request, Exception ex) {
        String error = "Skrsh neki na serveru: " + request.getRequestURI();
        return buildResponseEntity(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), error, ex.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorMessage errorMessage) {
        return new ResponseEntity<>(errorMessage, errorMessage.getStatus());
    }


}


