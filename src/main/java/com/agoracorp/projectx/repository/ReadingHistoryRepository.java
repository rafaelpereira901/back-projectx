package com.agoracorp.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agoracorp.projectx.model.ReadingHistory;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
	List<ReadingHistory> findByUserProfileId(Long profileId);

	List<ReadingHistory> findByUserProfileIdAndBookId(Long profileId, Long bookId);
}
