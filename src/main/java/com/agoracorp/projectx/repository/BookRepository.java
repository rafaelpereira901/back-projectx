package com.agoracorp.projectx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String>{}
