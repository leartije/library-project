package com.reciklaza.libraryproject.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDto {

    private String name;
    private String lastname;


}
