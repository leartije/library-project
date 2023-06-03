package com.reciklaza.libraryproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "title can't be blank")
    @Size(min = 1, max = 250)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @NotBlank(message = "ISBN can't be blank")
    @Column(unique = true, nullable = false)
    private String ISBN;

    @Column(nullable = false)
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
