package de.test.url.shortener.controller;

import java.util.List;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;
import de.test.url.shortener.service.URLShortenerService;
import io.swagger.annotations.Api;

@Api(value = "REST APIs related for URL Shortener")
@RestController
@RequestMapping("/api/tinyurl")
public class URLShortenerController {

	private static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";

	private static final String HAS_ROLE_ADMIN_OR_HAS_ROLE_USER = "hasRole('ADMIN') or hasRole('USER')";

	public final UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

	@Autowired
	private URLShortenerService urlShortenerService;

	@PreAuthorize(HAS_ROLE_ADMIN_OR_HAS_ROLE_USER)
	@PostMapping
	public URLDetails create(@RequestBody final String url) {

		Optional.of(url).filter(StringUtils::hasText).map(String::trim).filter(urlValidator::isValid)
				.orElseThrow(() -> new IllegalArgumentException("Invalid URL - " + url));

		return urlShortenerService.create(SecurityContextHolder.getContext().getAuthentication().getName(), url);
	}

	@PreAuthorize(HAS_ROLE_ADMIN_OR_HAS_ROLE_USER)
	@GetMapping("/{id}")
	public String get(@PathVariable final String id) {

		return Optional.of(id).filter(StringUtils::hasText).map(String::trim)
				.map(tinyURL -> urlShortenerService
						.get(SecurityContextHolder.getContext().getAuthentication().getName(), id))
				.orElseThrow(() -> new IllegalArgumentException("Invalid Tiny URL - " + id));
	}

	@PreAuthorize(HAS_ROLE_ADMIN)
	@GetMapping("/statistics")
	public List<Statistics> getAllStatistics() {
		return urlShortenerService.getAllStatistics();
	}

	@PreAuthorize(HAS_ROLE_ADMIN_OR_HAS_ROLE_USER)
	@GetMapping("/user/statistics")
	public List<Statistics> getUserStatistics() {
		return urlShortenerService.getUserStatistics(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@PreAuthorize(HAS_ROLE_ADMIN)
	@GetMapping("/user/{userId}/statistics")
	public List<Statistics> getUserStatisticsForAdmin(@PathVariable final String userId) {
		Optional.of(userId).filter(StringUtils::hasText).map(String::trim)
				.orElseThrow(() -> new IllegalArgumentException("Invalid userId - " + userId));
		return urlShortenerService.getUserStatistics(userId);
	}

}
