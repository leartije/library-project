package com.reciklaza.libraryproject.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "ddMMyyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String description;
}
