package com.reciklaza.libraryproject.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDto {

    private Long id;
    private String title;
    private String author;
    private String ISBN;
    private boolean available;

}
