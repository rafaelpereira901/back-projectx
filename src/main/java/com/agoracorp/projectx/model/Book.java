package com.agoracorp.projectx.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "ISBN is required")
	@Size(max = 30, message = "ISBN must have at most 30 characters")
	private String isbn;
	
	@NotBlank(message = "Book title is required")
	@Size(max = 200, message = "Book title must have at most 200 characters")
	private String title;
	@NotBlank(message = "Book description is required")
	@Size(max = 2000, message = "Book description must have at most 2000 characters")
	private String description;

	@ManyToOne
	@JoinColumn(name = "author_id")
	@JsonIgnoreProperties("books")
	@NotNull(message = "Author is required")
	@Valid
	private Author author;

	@ElementCollection(fetch = FetchType.EAGER)
	@Size(max = 20, message = "Genres must contain at most 20 items")
	private List<@NotBlank(message = "Genre value cannot be blank") String> genres;

	@NotNull(message = "Published date is required")
	@PastOrPresent(message = "Published date must be in the past or present")
	private LocalDate publishedDate;
	
}
