package com.agoracorp.projectx.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_book_shelves", uniqueConstraints = {
		@UniqueConstraint(name = "uk_shelf_profile_book", columnNames = { "profile_id", "book_id" })
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserBookShelf {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "profile_id", nullable = false)
	@NotNull(message = "Profile is required")
	private UserProfile userProfile;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	@NotNull(message = "Book is required")
	private Book book;

	@Column(nullable = false, length = 40)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Shelf status is required")
	private ShelfStatus status;

	@Column(nullable = false)
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		if (updatedAt == null) {
			updatedAt = Instant.now();
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}
}
