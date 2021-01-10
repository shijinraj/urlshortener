package de.test.url.shortener.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;
import de.test.url.shortener.service.URLShortenerService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = URLShortenerController.class)
class URLShortenerControllerTest {

	private static final String INVALID_USER = "invalidUser";

	private static final String USER_ADMIN = "admin";

	private static final String NORMAL_USER = "user";

	private static final String VALID_URL = "http://www.google.com/";

	private static final String ROLE_INVALID = "ROLE_INVALID";

	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final String ROLE_USER = "ROLE_USER";

	private static String BASE_URL = "/api/tinyurl";

	private static final String USER_STATISTICS_URL = BASE_URL + "/user/statistics";

	private static final String GET_USER_STATISTICS_FOR_ADMIN = BASE_URL + "/user/user/statistics";

	private static final String UTF_8 = "utf-8";

	private static final String VALID_TINY_URL_ID = "e02a1c6b-9574-4178-b01a-88d9c2ccf1e2";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private URLShortenerService urlShortenerService;

	@Autowired
	private ObjectMapper objectMapper;

	@WithMockUser(username = INVALID_USER, authorities = { ROLE_INVALID })
	@Test
	@DisplayName("Test Create Tiny URL for Unauthorized User")
	void testCreateForInvalidUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(post(BASE_URL).content(VALID_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Create Tiny URL without any URL input")
	void testCreateForNormalUserWithNoURL() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(post(BASE_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Required request body is missing")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Create Tiny URL with invalid URL input")
	void testCreateForNormalUserWithInvalidURL() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(post(BASE_URL).content("abcde").characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isBadRequest()).andExpect(content().string(containsString("Invalid URL")));
	}

	@WithMockUser(username = USER_ADMIN, authorities = { ROLE_ADMIN })
	@Test
	@DisplayName("Test Create Tiny URL for Admin User")
	void testCreateForAdminWithValidURL() throws JsonProcessingException, Exception {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(USER_ADMIN)
				.id("e02a1c6b-9574-4178-b01a-88d9c2ccf1e2").build();
		when(urlShortenerService.create(anyString(), eq(urlDetailsExpected.getUrl()))).thenReturn(urlDetailsExpected);

		// When & Then
		mockMvc.perform(post(BASE_URL).content(VALID_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(urlDetailsExpected))));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Create Tiny URL for Normal User")
	void testCreateForNormalUserWithValidURL() throws JsonProcessingException, Exception {
		// Given
		URLDetails urlDetailsExpected = URLDetails.builder().url(VALID_URL).userId(NORMAL_USER).id(VALID_TINY_URL_ID)
				.build();
		when(urlShortenerService.create(anyString(), eq(urlDetailsExpected.getUrl()))).thenReturn(urlDetailsExpected);

		// When & Then
		mockMvc.perform(post(BASE_URL).content(VALID_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(urlDetailsExpected))));
	}

	@WithMockUser(username = INVALID_USER, authorities = { ROLE_INVALID })
	@Test
	@DisplayName("Test Get Tiny URL for Unauthorized User")
	void testGetForInvalidUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(BASE_URL + "/" + VALID_TINY_URL_ID).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get Tiny URL for normal user without URL id")
	void tesGetForNormalUserWithNotTinyURLId() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(BASE_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Request method 'GET' not supported")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get Tiny URL for admin user without URL id")
	void tesGetForAdminUserWithNotTinyURLId() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(BASE_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Request method 'GET' not supported")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get Tiny URL for normal user with valid URL id")
	void tesGetForNormalUserWithValidTinyURLId() throws JsonProcessingException, Exception {
		// Given
		when(urlShortenerService.get(eq(NORMAL_USER), eq(VALID_TINY_URL_ID))).thenReturn(VALID_URL);

		// When & Then
		mockMvc.perform(get(BASE_URL + "/" + VALID_TINY_URL_ID).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(containsString(VALID_URL)));

	}

	@WithMockUser(username = USER_ADMIN, authorities = { ROLE_ADMIN })
	@Test
	@DisplayName("Test Get Tiny URL for admin user with valid URL id")
	void tesGetForAdminUserWithValidTinyURLId() throws JsonProcessingException, Exception {

		// Given
		when(urlShortenerService.get(eq(USER_ADMIN), eq(VALID_TINY_URL_ID))).thenReturn(VALID_URL);

		// When & Then
		mockMvc.perform(get(BASE_URL + "/" + VALID_TINY_URL_ID).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(containsString(VALID_URL)));

	}

	@WithMockUser(username = INVALID_USER, authorities = { ROLE_INVALID })
	@Test
	@DisplayName("Test Get All Statistics for Unauthorized User")
	void testGetAllStatisticsForInvalidUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(BASE_URL + "/statistics").characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get All Statistics for Normal User")
	void testGetAllStatisticsForNormalUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(BASE_URL + "/statistics").characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = USER_ADMIN, authorities = { ROLE_ADMIN })
	@Test
	@DisplayName("Test Get All Statistics for Admin User")
	void testGetAllStatisticsForAdminUser() throws JsonProcessingException, Exception {
		// Given
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(VALID_URL).creationCount(1l).callCount(1l).build());

		when(urlShortenerService.getAllStatistics()).thenReturn(statisticsListExpected);

		// When & Then
		mockMvc.perform(get(BASE_URL + "/statistics").characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(statisticsListExpected))));
	}

	@WithMockUser(username = INVALID_USER, authorities = { ROLE_INVALID })
	@Test
	@DisplayName("Test Get User Statistics for Unauthorized User")
	void testGetUserStatisticsForInvalidUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(USER_STATISTICS_URL).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get User Statistics for normal user")
	void testGetUserStatisticsForNormalUser() throws JsonProcessingException, Exception {
		// Given
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(VALID_URL).creationCount(1l).callCount(1l).build());

		when(urlShortenerService.getUserStatistics(NORMAL_USER)).thenReturn(statisticsListExpected);

		// When & Then
		mockMvc.perform(get(USER_STATISTICS_URL).characterEncoding(UTF_8)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(statisticsListExpected))));
	}

	@WithMockUser(username = USER_ADMIN, authorities = { ROLE_ADMIN })
	@Test
	@DisplayName("Test Get User Statistics for Admin user")
	void testGetUserStatisticsForAdminUser() throws JsonProcessingException, Exception {
		// Given
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(VALID_URL).creationCount(1l).callCount(1l).build());

		when(urlShortenerService.getUserStatistics(USER_ADMIN)).thenReturn(statisticsListExpected);

		// When & Then
		mockMvc.perform(get(USER_STATISTICS_URL).characterEncoding(UTF_8)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(statisticsListExpected))));
	}

	@WithMockUser(username = INVALID_USER, authorities = { ROLE_INVALID })
	@Test
	@DisplayName("Test Get User Statistics for Admin by Invalid User")
	void testGetUserStatisticsForAdminByInvalidUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(GET_USER_STATISTICS_FOR_ADMIN).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = NORMAL_USER, authorities = { ROLE_USER })
	@Test
	@DisplayName("Test Get User Statistics for Admin by Normal User")
	void testGetUserStatisticsForAdminByNormalUser() throws JsonProcessingException, Exception {
		// When & Then
		mockMvc.perform(get(GET_USER_STATISTICS_FOR_ADMIN).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString("Access is denied")));
	}

	@WithMockUser(username = USER_ADMIN, authorities = { ROLE_ADMIN })
	@Test
	@DisplayName("Test Get User Statistics for Admin by Admin User")
	void testGetUserStatisticsForAdminByAdminUser() throws JsonProcessingException, Exception {
		// Given
		List<Statistics> statisticsListExpected = Collections
				.singletonList(Statistics.builder().url(VALID_URL).creationCount(1l).callCount(1l).build());

		when(urlShortenerService.getUserStatistics(NORMAL_USER)).thenReturn(statisticsListExpected);

		// When & Then
		mockMvc.perform(get(GET_USER_STATISTICS_FOR_ADMIN).characterEncoding(UTF_8)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(statisticsListExpected))));
	}

}
