package com.reciklaza.libraryproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "first name can't be blank")
    private String firstName;

    @NotBlank(message = "last name can't be blank")
    private String lastName;

    @NotBlank(message = "password can't be blank")
    private String password;

    @NotBlank(message = "email can't be blank")
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Book> books;

}
