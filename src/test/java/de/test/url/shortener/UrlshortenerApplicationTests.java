package de.test.url.shortener;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.test.url.shortener.exception.ErrorDetails;
import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class UrlshortenerApplicationTests {

	private static final String VALID_URL_7 = "https://koblenz-bringts.de/";
	private static final String VALID_URL_6 = "https://news.google.com/";
	private static final String VALID_URL_5 = "http://news.google.de/";
	private static final String VALID_URL_4 = "https://www.amazon.de";
	private static final String VALID_URL_3 = "https://www.testo.com";
	private static final String VALID_URL_2 = "http://www.gmail.com/";
	private static final String NORMAL_USER_PASSWORD = "user";
	private final String LOCAL_HOST = "http://localhost:";
	private String BASE_URL = "/api/tinyurl";
	@LocalServerPort
	private int randomServerPort;

	private static final String UNAUTHORIZED_USER = "invalidUser";

	private static final String UNAUTHORIZED_USER_PASSWORD = "test";

	private static final String VALID_URL = "http://www.google.com/";

	private static final String NORMAL_USER = NORMAL_USER_PASSWORD;

	private static final String USER_ADMIN = "admin";

	private static final String USER_ADMIN_PASSWORD = USER_ADMIN;

	private static final String VALID_TINY_URL_ID = "e02a1c6b-9574-4178-b01a-88d9c2ccf1e2";

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeAll
	void setUpBeforeClass() throws Exception {
		BASE_URL = LOCAL_HOST + randomServerPort + BASE_URL;
	}

	@Test
	@DisplayName("Test Create Tiny URL for Unauthorized User")
	void testCreateForInvalidUser() {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(UNAUTHORIZED_USER, UNAUTHORIZED_USER_PASSWORD)
				.postForEntity(BASE_URL, VALID_URL, URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Create Tiny URL without any URL input")
	void testCreateForNormalUserWithNoURL() {
		// Given & When
		ResponseEntity<ErrorDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.postForEntity(BASE_URL, null, ErrorDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(result.getBody().getMoreInfo()).contains("Required request body is missing");

	}

	@Test
	@DisplayName("Test Create Tiny URL with invalid URL input")
	void testCreateForNormalUserWithInvalidURL() {
		// Given & When
		ResponseEntity<ErrorDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.postForEntity(BASE_URL, "abcde", ErrorDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(result.getBody().getMoreInfo()).contains("Invalid URL");

	}

	@Test
	@DisplayName("Test Create Tiny URL for Admin User")
	void testCreateForAdminWithValidURL() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL_2).userId(USER_ADMIN).creationCount(1l)
				.build();

		// When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD)
				.postForEntity(BASE_URL, VALID_URL_2, URLDetails.class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getId()).isNotBlank();
		assertThat(result.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(urlDetailsExpected);
	}

	@Test
	@DisplayName("Test Create Tiny URL for Normal User")
	void testCreateForNormalUserWithValidURL() {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL_3).userId(NORMAL_USER).creationCount(1l)
				.build();

		// When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.postForEntity(BASE_URL, VALID_URL_3, URLDetails.class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getId()).isNotBlank();
		assertThat(result.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(urlDetailsExpected);
	}

	@Test
	@DisplayName("Test Get Tiny URL for Unauthorized User")
	void testGetForInvalidUser() {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(UNAUTHORIZED_USER, UNAUTHORIZED_USER_PASSWORD)
				.getForEntity(BASE_URL + "/" + VALID_TINY_URL_ID, URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get Tiny URL for normal user without URL id")
	void tesGetForNormalUserWithNotTinyURLId() {
		// Given & When
		ResponseEntity<ErrorDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.getForEntity(BASE_URL, ErrorDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(result.getBody().getMoreInfo()).contains("Request method 'GET' not supported");
	}

	@Test
	@DisplayName("Test Get Tiny URL for admin user without URL id")
	void tesGetForAdminUserWithNotTinyURLId() {
		// Given & When
		ResponseEntity<ErrorDetails> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD)
				.getForEntity(BASE_URL, ErrorDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(result.getBody().getMoreInfo()).contains("Request method 'GET' not supported");
	}

	@Test
	@DisplayName("Test Get Tiny URL for normal user with valid URL id")
	void tesGetForNormalUserWithValidTinyURLId() {
		// Given
		ResponseEntity<URLDetails> createTinyURLDetails = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.postForEntity(BASE_URL, VALID_URL, URLDetails.class);

		// Given & When
		ResponseEntity<String> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.getForEntity(BASE_URL + "/" + createTinyURLDetails.getBody().getId(), String.class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotBlank();
		assertThat(result.getBody()).isEqualTo(VALID_URL);

	}

	@Test
	@DisplayName("Test Get Tiny URL for admin user with valid URL id")
	void tesGetForAdminUserWithValidTinyURLId() {
		// Given
		ResponseEntity<URLDetails> createTinyURLDetails = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN)
				.postForEntity(BASE_URL, VALID_URL, URLDetails.class);

		// Given & When
		ResponseEntity<String> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN)
				.getForEntity(BASE_URL + "/" + createTinyURLDetails.getBody().getId(), String.class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotBlank();
		assertThat(result.getBody()).isEqualTo(VALID_URL);

	}

	@Test
	@DisplayName("Test Get All Statistics for Unauthorized User")
	void testGetAllStatisticsForInvalidUser() {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(UNAUTHORIZED_USER, UNAUTHORIZED_USER_PASSWORD)
				.getForEntity(BASE_URL + "/statistics", URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get All Statistics for Normal User")
	void testGetAllStatisticsForNormalUser() throws JsonProcessingException, Exception {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.getForEntity(BASE_URL + "/statistics", URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get All Statistics for Admin User")
	void testGetAllStatisticsForAdminUser() throws JsonProcessingException, Exception {
		// Given
		Statistics statisticsListExpected = Statistics.builder().url(VALID_URL_4).creationCount(1l).callCount(0l)
				.build();

		restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD).postForEntity(BASE_URL, VALID_URL_4,
				URLDetails.class);

		// When
		ResponseEntity<Statistics[]> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD)
				.getForEntity(BASE_URL + "/statistics", Statistics[].class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(Arrays.asList(result.getBody())).contains(statisticsListExpected);
	}

	@Test
	@DisplayName("Test Get User Statistics for Unauthorized User")
	void testGetUserStatisticsForInvalidUser() throws JsonProcessingException, Exception {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(UNAUTHORIZED_USER, UNAUTHORIZED_USER_PASSWORD)
				.getForEntity(BASE_URL + "/user/statistics", URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get User Statistics for normal user")
	void testGetUserStatisticsForNormalUser() throws JsonProcessingException, Exception {
		// Given
		Statistics statisticsListExpected = Statistics.builder().url(VALID_URL_5).creationCount(1l).callCount(0l)
				.build();

		restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD).postForEntity(BASE_URL, VALID_URL_5,
				URLDetails.class);

		// When
		ResponseEntity<Statistics[]> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.getForEntity(BASE_URL + "/user/statistics", Statistics[].class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(Arrays.asList(result.getBody())).contains(statisticsListExpected);
	}

	@Test
	@DisplayName("Test Get User Statistics for Admin user")
	void testGetUserStatisticsForAdminUser() throws JsonProcessingException, Exception {
		// Given
		Statistics statisticsListExpected = Statistics.builder().url(VALID_URL_6).creationCount(1l).callCount(0l)
				.build();

		restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD).postForEntity(BASE_URL, VALID_URL_6,
				URLDetails.class);

		// When
		ResponseEntity<Statistics[]> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD)
				.getForEntity(BASE_URL + "/user/statistics", Statistics[].class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(Arrays.asList(result.getBody())).contains(statisticsListExpected);
	}

	@Test
	@DisplayName("Test Get User Statistics for Admin by Invalid User")
	void testGetUserStatisticsForAdminByInvalidUser() throws JsonProcessingException, Exception {

		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(UNAUTHORIZED_USER, UNAUTHORIZED_USER_PASSWORD)
				.getForEntity(BASE_URL + "/user/user/statistics", URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get User Statistics for Admin by Normal User")
	void testGetUserStatisticsForAdminByNormalUser() throws JsonProcessingException, Exception {
		// Given & When
		ResponseEntity<URLDetails> result = restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD)
				.getForEntity(BASE_URL + "/user/user/statistics", URLDetails.class);
		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@DisplayName("Test Get User Statistics for Admin by Admin User")
	void testGetUserStatisticsForAdminByAdminUser() throws JsonProcessingException, Exception {
		// Given
		Statistics statisticsListExpected = Statistics.builder().url(VALID_URL_7).creationCount(1l).callCount(0l)
				.build();

		restTemplate.withBasicAuth(NORMAL_USER, NORMAL_USER_PASSWORD).postForEntity(BASE_URL, VALID_URL_7,
				URLDetails.class);

		// When
		ResponseEntity<Statistics[]> result = restTemplate.withBasicAuth(USER_ADMIN, USER_ADMIN_PASSWORD)
				.getForEntity(BASE_URL + "/user/" + NORMAL_USER + "/statistics", Statistics[].class);

		// Then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(Arrays.asList(result.getBody())).contains(statisticsListExpected);
	}

}
