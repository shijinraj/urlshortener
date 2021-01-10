package de.test.url.shortener.service;

import java.util.List;

import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;

public interface URLShortenerService {
	URLDetails create(String userId, String url);
	
	String get(String userId, String id);
	
	List<Statistics> getUserStatistics(String userId);
	
	List<Statistics> getAllStatistics();
}
