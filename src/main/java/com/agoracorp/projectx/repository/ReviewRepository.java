package com.agoracorp.projectx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByUserProfileId(Long profileId);

	List<Review> findByBookId(Long bookId);

	Optional<Review> findByUserProfileIdAndBookId(Long profileId, Long bookId);
}
