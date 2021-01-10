package de.test.url.shortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.test.url.shortener.repository.domain.URLDetails;

public interface URLShortenerRepository extends JpaRepository<URLDetails, String> {
	
	Optional<URLDetails> findByUserIdAndUrl(String userId,String url);
	Optional<URLDetails> findByUserIdAndId(String userId,String id);
	Optional<List<URLDetails>> findByUserId(String userId);

}
