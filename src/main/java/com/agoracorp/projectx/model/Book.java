package com.agoracorp.projectx.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
	@GeneratedValue
	private String id;
	private String isbn;
	private String title;
	private String description;
	private String author;
	private List<String> genres;
	private LocalDate publishedDate;
	
}
