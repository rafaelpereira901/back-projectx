package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.repository.BookRepository;

@Service
public class BookService {

	final private BookRepository bookRepository;
	
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}	
	
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Book getBookById(String id) {
		return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book with id " + id + " not found!"));
	}
	
	public Book createBook() {
		Book newBook = new Book();
		
		newBook.setTitle("Irmaos Metralha");
		newBook.setDescription("livro foda");
		newBook.setAuthor(null);
		
		return bookRepository.save(newBook);
	}

}
