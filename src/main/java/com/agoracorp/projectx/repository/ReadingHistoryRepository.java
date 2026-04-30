package com.agoracorp.projectx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.ReadingHistory;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
	List<ReadingHistory> findByUserProfileId(Long profileId);

	List<ReadingHistory> findByUserProfileIdAndBookId(Long profileId, Long bookId);

	List<ReadingHistory> findByBookIdOrderByCreatedAtDesc(Long bookId);

	Optional<ReadingHistory> findFirstByBookIdOrderByCreatedAtDesc(Long bookId);
}
