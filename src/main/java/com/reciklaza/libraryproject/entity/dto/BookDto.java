package com.reciklaza.libraryproject.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookDto {

    private Long id;
    private String title;
    private String author;
    private String ISBN;
    private boolean available;

}
