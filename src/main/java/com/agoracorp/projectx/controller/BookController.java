package com.agoracorp.projectx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	public ResponseEntity<Book> getBookById(@PathVariable String id){
		return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
	}
	
	@PostMapping()
	public ResponseEntity<Book> createBook(){
		return new ResponseEntity<>(bookService.createBook(), HttpStatus.CREATED);
	}

}
