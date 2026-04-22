package com.agoracorp.projectx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
