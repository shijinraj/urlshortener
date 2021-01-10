package de.test.url.shortener.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.test.url.shortener.repository.URLShortenerRepository;
import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;

@ExtendWith(SpringExtension.class)
@DisplayName("URL Shortener Service Test")
class URLShortenerServiceTest {

	private static final String VALID_TINY_URL_ID = "e02a1c6b-9574-4178-b01a-88d9c2ccf1e2";

	private static final String VALID_USER = "user";

	private static final String VALID_URL = "http://www.google.com/";

	@Mock
	private URLShortenerRepository repository;

	@InjectMocks
	private URLShortenerService urlShortenerService = new URLShortenerServiceImpl();

	@Test
	@DisplayName("Test URL Shortener Creation with empty user id")
	void testCreateWithEmptyUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.create("", VALID_URL));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id ");
	}

	@Test
	@DisplayName("Test URL Shortener Creation with null user id")
	void testCreateWithNullUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.create(null, VALID_URL));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id");
	}

	@Test
	@DisplayName("Test URL Shortener Creation with empty URL")
	void testCreateWithEmptyURL() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.create(VALID_USER, ""));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid URL ");
	}

	@Test
	@DisplayName("Test URL Shortener Creation with null URL")
	void testCreateWithNullURL() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.create(VALID_USER, null));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid URL");
	}

	@Test
	@DisplayName("Test URL Shortener Creation with one existing valid user id and URL")
	void testCreate() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(VALID_USER)
				.id(VALID_TINY_URL_ID).build();
		when(repository.findByUserIdAndUrl(urlDetailsExpected.getUserId(), urlDetailsExpected.getUrl()))
				.thenReturn(Optional.of(urlDetailsExpected));
		urlDetailsExpected.setCreationCount(urlDetailsExpected.getCreationCount() + 1);
		when(repository.save(urlDetailsExpected)).thenReturn(urlDetailsExpected);

		// When
		URLDetails urlDetailsActual = urlShortenerService.create(urlDetailsExpected.getUserId(),
				urlDetailsExpected.getUrl());

		// Then
		Assertions.assertThat(urlDetailsActual).isEqualTo(urlDetailsExpected);
	}

	@Test
	@DisplayName("Test URL Shortener Creation with new valid user id and URL")
	void testCreateNew() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(VALID_USER)
				.id(VALID_TINY_URL_ID).creationCount(1l).build();

		when(repository.findByUserIdAndUrl(urlDetailsExpected.getUserId(), urlDetailsExpected.getUrl()))
				.thenReturn(Optional.empty());
		when(repository.save(any(URLDetails.class))).thenReturn(urlDetailsExpected);

		// When
		URLDetails urlDetailsActual = urlShortenerService.create(urlDetailsExpected.getUserId(),
				urlDetailsExpected.getUrl());

		// Then
		Assertions.assertThat(urlDetailsActual).isEqualTo(urlDetailsExpected);
	}

	@Test
	@DisplayName("Test URL Shortener Get with null user id and id")
	void testGetWithIvalidNullUserIdAndId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get(null, null));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id ");
	}

	@Test
	@DisplayName("Test URL Shortener Get with null user id")
	void testGetWithIvalidNullUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get(null, VALID_TINY_URL_ID));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id ");
	}

	@Test
	@DisplayName("Test URL Shortener Get with null id")
	void testGetWithIvalidNullId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get(VALID_USER, null));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid id");
	}

	@Test
	@DisplayName("Test URL Shortener Get with empty user id")
	void testGetWithIvalidEmptyUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get("", VALID_TINY_URL_ID));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id ");
	}

	@Test
	@DisplayName("Test URL Shortener Get with empty id")
	void testGetWithIvalidEmptyId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get(VALID_USER, ""));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid id ");
	}

	@Test
	@DisplayName("Test URL Shortener Get with empty user id and id")
	void testGetWithIvalidEmptyUserIdAndId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.get("", ""));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id ");
	}

	@Test
	@DisplayName("Test URL Shortener Get with invalid user id")
	void testGetWithIvalidUserId() {
		// Given
		String invalidUserId = "ivaliduser";
		String id = VALID_TINY_URL_ID;
		when(repository.findByUserIdAndId(invalidUserId, id))
				.thenThrow(new NoResultException("No URL available for the userid " + invalidUserId + " id " + id));
		// When and Then
		NoResultException noResultException = assertThrows(NoResultException.class,
				() -> urlShortenerService.get(invalidUserId, id));
		Assertions.assertThat(noResultException)
				.hasMessageContaining("No URL available for the userid " + invalidUserId + " id " + id);
	}

	@Test
	@DisplayName("Test URL Shortener Get with invalid id")
	void testGetWithInvalidId() {
		// Given
		String userId = VALID_USER;
		String invalidId = "abcd";
		when(repository.findByUserIdAndId(userId, invalidId))
				.thenThrow(new NoResultException("No URL available for the userid " + userId + " id " + invalidId));
		// When and Then
		NoResultException noResultException = assertThrows(NoResultException.class,
				() -> urlShortenerService.get(userId, invalidId));
		Assertions.assertThat(noResultException)
				.hasMessageContaining("No URL available for the userid " + userId + " id " + invalidId);
	}

	@Test
	@DisplayName("Test URL Shortener Get with valid user id and id")
	void testGet() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(VALID_USER)
				.id(VALID_TINY_URL_ID).creationCount(1l).build();

		when(repository.findByUserIdAndId(urlDetailsExpected.getUserId(), urlDetailsExpected.getId()))
				.thenReturn(Optional.of(urlDetailsExpected));

		urlDetailsExpected.setCallCount(1l);

		when(repository.save(any(URLDetails.class))).thenReturn(urlDetailsExpected);

		// When
		String urlActual = urlShortenerService.get(urlDetailsExpected.getUserId(), urlDetailsExpected.getId());

		// Then
		Assertions.assertThat(urlActual).isEqualTo(urlDetailsExpected.getUrl());

	}

	@Test
	@DisplayName("Test URL Shortener Get User Statistics with null user id")
	void testGetUserStatisticsWithNullUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.getUserStatistics(null));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id");

	}

	@Test
	@DisplayName("Test URL Shortener Get User Statistics with empty user id")
	void testGetUserStatisticsWithEmptyUserId() {
		// When and Then
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> urlShortenerService.getUserStatistics(null));
		Assertions.assertThat(illegalArgumentException).hasMessageContaining("Invalid user id");

	}

	@Test
	@DisplayName("Test URL Shortener Get User Statistics with invalid user id")
	void testGetUserStatisticsWithInvalidUserId() {
		// Given
		String invalidUserId = "abcd";

		when(repository.findByUserId(invalidUserId))
				.thenThrow(new NoResultException("No details available for the user id " + invalidUserId));

		// When and Then
		NoResultException noResultException = assertThrows(NoResultException.class,
				() -> urlShortenerService.getUserStatistics(invalidUserId));
		Assertions.assertThat(noResultException)
				.hasMessageContaining("No details available for the user id " + invalidUserId);

	}

	@Test
	@DisplayName("Test URL Shortener Get User Statistics with valid user id and empty data")
	void testGetUserStatisticsWithEmptyData() {
		// Given
		when(repository.findByUserId(VALID_USER)).thenReturn(Optional.empty());

		// When and Then
		NoResultException noResultException = assertThrows(NoResultException.class,
				() -> urlShortenerService.getUserStatistics(VALID_USER));
		Assertions.assertThat(noResultException).hasMessageContaining("No details available for the user id user");

	}

	@Test
	@DisplayName("Test URL Shortener Get User Statistics with valid user id")
	void testGetUserStatistics() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(VALID_USER)
				.id(VALID_TINY_URL_ID).creationCount(1l).build();

		when(repository.findByUserId(urlDetailsExpected.getUserId()))
				.thenReturn(Optional.of(Collections.singletonList(urlDetailsExpected)));
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(urlDetailsExpected.getUrl()).creationCount(1l).build());

		// When
		List<Statistics> statisticsListActual = urlShortenerService.getUserStatistics(urlDetailsExpected.getUserId());

		// Then
		Assertions.assertThat(statisticsListActual).isEqualTo(statisticsListExpected);

	}

	@Test
	@DisplayName("Test URL Shortener Get All User Statistics with valid user id")
	void testGetAllStatistics() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(VALID_USER)
				.id(VALID_TINY_URL_ID).creationCount(1l).build();

		when(repository.findAll()).thenReturn(Collections.singletonList(urlDetailsExpected));
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(urlDetailsExpected.getUrl()).creationCount(1l).build());

		// When
		List<Statistics> statisticsListActual = urlShortenerService.getAllStatistics();

		// Then
		Assertions.assertThat(statisticsListActual).isEqualTo(statisticsListExpected);
	}

	@Test
	@DisplayName("Test URL Shortener Get All User Statistics with valid user id And No details available")
	void testGetAllStatisticsWithEmptyData() {
		// When
		List<Statistics> statisticsListActual = urlShortenerService.getAllStatistics();

		// Then
		Assertions.assertThat(statisticsListActual).isEqualTo(Collections.emptyList());
	}

}
