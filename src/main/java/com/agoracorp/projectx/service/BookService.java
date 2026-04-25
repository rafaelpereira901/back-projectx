package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.repository.BookRepository;

@Service
public class BookService {

	final private BookRepository bookRepository;
	
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}
	
	@Transactional(readOnly = true)
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Book getBookById(Long id) {
		return findByIdOrThrow(id);
	}

	@Transactional
	public Book createBook(Book book) {
		book.setId(null);
		return bookRepository.save(book);
	}

	@Transactional
	public Book updateBook(Long id, Book updatedBook) {
		Book existingBook = findByIdOrThrow(id);

		existingBook.setIsbn(updatedBook.getIsbn());
		existingBook.setTitle(updatedBook.getTitle());
		existingBook.setDescription(updatedBook.getDescription());
		existingBook.setAuthor(updatedBook.getAuthor());
		existingBook.setGenres(updatedBook.getGenres());
		existingBook.setPublishedDate(updatedBook.getPublishedDate());

		return bookRepository.save(existingBook);
	}

	@Transactional
	public void deleteBook(Long id) {
		Book existingBook = findByIdOrThrow(id);
		bookRepository.delete(existingBook);
	}

	private Book findByIdOrThrow(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + id + " not found"));
	}

}
