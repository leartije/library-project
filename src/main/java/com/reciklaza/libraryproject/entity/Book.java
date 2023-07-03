package com.reciklaza.libraryproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.reciklaza.libraryproject.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOOKS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "author_id")
    //@JsonIgnoreProperties("books")
    @JsonManagedReference
    private Author author;
    private String ISBN;
    private boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("books")
    private User user;



}
