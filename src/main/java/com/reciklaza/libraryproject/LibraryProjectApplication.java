package com.reciklaza.libraryproject;

import com.reciklaza.libraryproject.entity.Book;
import com.reciklaza.libraryproject.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class LibraryProjectApplication implements ApplicationRunner {

	private final BookRepository bookRepository;

	public static void main(String[] args) {
		SpringApplication.run(LibraryProjectApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Book book = Book.builder()
				.title("naslov")
				.author("pisac")
				.ISBN("12345")
				.available(true)
				.build();

		bookRepository.save(book);
		log.info("snimio je knjigu u bazu {}", book);
	}
}
