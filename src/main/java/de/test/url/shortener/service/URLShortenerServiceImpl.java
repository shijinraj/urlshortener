package de.test.url.shortener.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.test.url.shortener.repository.URLShortenerRepository;
import de.test.url.shortener.repository.domain.Statistics;
import de.test.url.shortener.repository.domain.URLDetails;

@Service
public class URLShortenerServiceImpl implements URLShortenerService {

	@Autowired
	private URLShortenerRepository repository;

	@Override
	public URLDetails create(String userId, String url) {

		Assert.isTrue(StringUtils.hasText(userId), "Invalid user id " + userId);
		Assert.isTrue(StringUtils.hasText(url), "Invalid URL " + url);

		URLDetails urlDetails = repository.findByUserIdAndUrl(userId, url).orElse(null);

		if (Optional.ofNullable(urlDetails).isPresent()) {
			urlDetails.setCreationCount(urlDetails.getCreationCount() + 1);
		} else {
			urlDetails = URLDetails.builder().userId(userId).url(url).creationCount(1).build();
		}

		return repository.save(urlDetails);
	}

	@Override
	public String get(String userId, String id) {

		Assert.isTrue(StringUtils.hasText(userId), "Invalid user id " + userId);
		Assert.isTrue(StringUtils.hasText(id), "Invalid id " + id);

		URLDetails urlDetails = repository.findByUserIdAndId(userId, id)
				.orElseThrow(() -> new NoResultException("No URL available for the userid " + userId + " id " + id));

		urlDetails.setCallCount(urlDetails.getCallCount() + 1);

		return repository.save(urlDetails).getUrl();

	}

	@Override
	public List<Statistics> getUserStatistics(String userId) {
		Assert.isTrue(StringUtils.hasText(userId), "Invalid user id " + userId);
		List<URLDetails> urlDetailList = repository.findByUserId(userId)
				.orElseThrow(() -> new NoResultException("No details available for the user id " + userId));

		return getStatistics(urlDetailList);

	}

	private List<Statistics> getStatistics(List<URLDetails> urlDetailList) {
		Map<String, Long> groupByURLAndCreationCount = Optional.ofNullable(urlDetailList).map(Collection::stream)
				.orElseGet(Stream::empty).collect(Collectors.groupingBy(URLDetails::getUrl,
						Collectors.summingLong(URLDetails::getCreationCount)));

		Map<String, Long> groupByURLAndCallCount = Optional.ofNullable(urlDetailList).map(Collection::stream)
				.orElseGet(Stream::empty)
				.collect(Collectors.groupingBy(URLDetails::getUrl, Collectors.summingLong(URLDetails::getCallCount)));

		return groupByURLAndCreationCount.entrySet().stream()
				.map(urlCreationCountMap -> Statistics.builder().url(urlCreationCountMap.getKey())
						.creationCount(urlCreationCountMap.getValue())
						.callCount(groupByURLAndCallCount.get(urlCreationCountMap.getKey())).build())
				.collect(Collectors.toList());
	}

	@Override
	public List<Statistics> getAllStatistics() {
		return getStatistics(repository.findAll());
	}

}
