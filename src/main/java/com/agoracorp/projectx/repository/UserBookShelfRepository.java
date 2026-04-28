package com.agoracorp.projectx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.ShelfStatus;
import com.agoracorp.projectx.model.UserBookShelf;

@Repository
public interface UserBookShelfRepository extends JpaRepository<UserBookShelf, Long> {
	List<UserBookShelf> findByUserProfileIdAndStatus(Long profileId, ShelfStatus status);

	Optional<UserBookShelf> findByUserProfileIdAndBookId(Long profileId, Long bookId);
}
