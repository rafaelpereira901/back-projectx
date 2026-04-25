package com.agoracorp.projectx.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.repository.AuthorRepository;
import com.agoracorp.projectx.repository.BookRepository;

@Configuration
public class H2DataInitializer {

	@Value("${spring.datasource.url:}")
	private String datasourceUrl;

	@Bean
	CommandLineRunner seedH2Data(AuthorRepository authorRepository, BookRepository bookRepository) {
		return args -> {
			if (!datasourceUrl.startsWith("jdbc:h2:") || authorRepository.count() > 0 || bookRepository.count() > 0) {
				return;
			}

			Author dostoevysk = authorRepository
					.save(buildAuthor("Fyodor Dostoevysk", "Russian novelist known for psychological and existential fiction."));
			Author cristie = authorRepository
					.save(buildAuthor("Agatha Cristie", "English mystery writer and creator of iconic detective stories."));
			Author nietschez = authorRepository
					.save(buildAuthor("Friedrich Nietschez", "German philosopher and cultural critic of modernity and morality."));

			bookRepository.saveAll(List.of(
					buildBook("9780140449136", "Crime and Punishment",
							"A former student descends into guilt and moral conflict after committing a murder.",
							dostoevysk, List.of("Psychological", "Philosophical", "Classic"), LocalDate.of(1866, 1, 1)),
					buildBook("9780374528379", "Notes from Underground",
							"A bitter narrator reflects on free will, alienation, and the contradictions of modern life.",
							dostoevysk, List.of("Novella", "Philosophical", "Classic"), LocalDate.of(1864, 1, 1)),
					buildBook("9780374528379-2", "The Brothers Karamazov",
							"A family drama exploring faith, doubt, justice, and responsibility through three brothers.",
							dostoevysk, List.of("Philosophical", "Literary", "Classic"), LocalDate.of(1880, 1, 1)),

					buildBook("9780007119318", "Murder on the Orient Express",
							"Detective Hercule Poirot investigates a murder aboard a luxury train.",
							cristie, List.of("Mystery", "Crime", "Classic"), LocalDate.of(1934, 1, 1)),
					buildBook("9780062073488", "And Then There Were None",
							"Ten strangers are isolated on an island and eliminated one by one.",
							cristie, List.of("Mystery", "Thriller", "Classic"), LocalDate.of(1939, 1, 1)),
					buildBook("9780007527526", "The Murder of Roger Ackroyd",
							"Poirot unravels a village murder in one of the genre's most famous whodunits.",
							cristie, List.of("Mystery", "Detective", "Classic"), LocalDate.of(1926, 1, 1)),

					buildBook("9780140441185", "Thus Spoke Zarathustra",
							"A philosophical narrative introducing ideas such as the overman and self-overcoming.",
							nietschez, List.of("Philosophy", "Classic", "Essay"), LocalDate.of(1883, 1, 1)),
					buildBook("9780140449235", "Beyond Good and Evil",
							"Nietzsche critiques traditional morality and explores perspectives beyond binary ethics.",
							nietschez, List.of("Philosophy", "Ethics", "Classic"), LocalDate.of(1886, 1, 1)),
					buildBook("9780679724629", "On the Genealogy of Morality",
							"An analysis of how moral values evolved through history, power, and resentment.",
							nietschez, List.of("Philosophy", "Ethics", "Classic"), LocalDate.of(1887, 1, 1))));
		};
	}

	private Author buildAuthor(String name, String description) {
		Author author = new Author();
		author.setName(name);
		author.setDescription(description);
		return author;
	}

	private Book buildBook(String isbn, String title, String description, Author author, List<String> genres,
			LocalDate publishedDate) {
		Book book = new Book();
		book.setIsbn(isbn);
		book.setTitle(title);
		book.setDescription(description);
		book.setAuthor(author);
		book.setGenres(genres);
		book.setPublishedDate(publishedDate);
		return book;
	}
}