package com.agoracorp.projectx.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {
	
	final private BookService bookService;
	
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}
	
	@GetMapping
	public ResponseEntity<List<Book>> getBooks(){
		return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable Long id){
		return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
	}
	
	@PostMapping()
	public ResponseEntity<Book> createBook(@Valid @RequestBody Book book){
		return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book){
		return new ResponseEntity<>(bookService.updateBook(id, book), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id){
		bookService.deleteBook(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
