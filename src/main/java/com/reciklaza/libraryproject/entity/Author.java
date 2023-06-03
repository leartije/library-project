package com.reciklaza.libraryproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;

}
