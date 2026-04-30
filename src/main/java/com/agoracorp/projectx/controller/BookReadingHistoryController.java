package com.agoracorp.projectx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.dto.ReadingHistoryResponse;
import com.agoracorp.projectx.service.ReadingHistoryService;

@RestController
@RequestMapping("/books/{bookId}")
public class BookReadingHistoryController {

	private final ReadingHistoryService readingHistoryService;

	public BookReadingHistoryController(ReadingHistoryService readingHistoryService) {
		this.readingHistoryService = readingHistoryService;
	}

	@GetMapping("/reading-histories")
	public ResponseEntity<List<ReadingHistoryResponse>> getByBook(@PathVariable Long bookId) {
		return new ResponseEntity<>(readingHistoryService.getByBook(bookId), HttpStatus.OK);
	}

	@GetMapping("/reading-histories/latest")
	public ResponseEntity<ReadingHistoryResponse> getLatestByBook(@PathVariable Long bookId) {
		return new ResponseEntity<>(readingHistoryService.getLatestByBook(bookId), HttpStatus.OK);
	}
}
