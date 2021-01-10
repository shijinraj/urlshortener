package de.test.url.shortener.repository.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistics {
	private String url;
	private long creationCount;
	private long callCount;

}
