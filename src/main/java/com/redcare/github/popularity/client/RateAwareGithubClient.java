package com.redcare.github.popularity.client;

import com.redcare.github.popularity.exception.client.GithubNotModifiedException;
import com.redcare.github.popularity.exception.client.GithubRateLimitException;
import com.redcare.github.popularity.exception.client.GithubUnavailableException;
import com.redcare.github.popularity.exception.client.GithubValidationException;
import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.GithubSearchParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class RateAwareGithubClient implements GithubClient {
    private static final int MAX_REQUESTS_WITH_TOKEN = 30;
    private static final int MAX_REQUESTS_WITHOUT_TOKEN = 10;

    private final String accessToken;
    private final RestClient restClient;

    public RateAwareGithubClient(@Value("${github.access-token:''}") String accessToken, RestClient restClient) {
        this.accessToken = accessToken;
        this.restClient = restClient;
    }

    @Override
    public List<GithubRepository> getRepositories(GithubSearchParams searchParams) {
        // determine the maximum number of pages based on whether an access token is provided
        var cappedRequestCount = accessToken != null && !accessToken.isEmpty() ? MAX_REQUESTS_WITH_TOKEN : MAX_REQUESTS_WITHOUT_TOKEN;
        // cap the requested pages to the maximum allowed
        cappedRequestCount = Math.min(searchParams.maxPages(), cappedRequestCount);
        var firstResponse = fetchPage(searchParams, 1);
        List<GithubRepository> result = new ArrayList<>(firstResponse.repositories());
        // calculate how many more pages need to be fetched
        var totalPages = (int) Math.ceil((double) firstResponse.repoCount() / searchParams.pageSize());
        if (totalPages > 1) {
            // bound the capped request count to the total pages
            cappedRequestCount = Math.min(cappedRequestCount, totalPages);
            for (int page = 2; page <= cappedRequestCount; page++) {
                var response = fetchPage(searchParams, page);
                result.addAll(response.repositories());
            }
        }
        return result;
    }

    private GithubSearchResponse fetchPage(GithubSearchParams searchParams, int page) {
        return restClient.get()
                .uri(x -> getUri(searchParams, page, x))
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_MODIFIED),
                        (request, response) -> {
                            throw new GithubNotModifiedException("Resource not modified since last request");
                        })
                .onStatus(status -> status.equals(HttpStatus.UNPROCESSABLE_ENTITY),
                        (request, response) -> {
                            throw new GithubValidationException("Invalid request parameters for GitHub API");
                        })
                .onStatus(status -> status.equals(HttpStatus.FORBIDDEN),
                        (request, response) -> {
                            throw new GithubRateLimitException("Github API rate limit exceeded - try later or authenticate");
                        })
                .onStatus(status -> status.equals(HttpStatus.SERVICE_UNAVAILABLE),
                        (request, response) -> {
                            throw new GithubUnavailableException("GitHub API service is currently unavailable");
                        })
                .body(GithubSearchResponse.class);
    }

    private URI getUri(GithubSearchParams searchParams, int page, UriBuilder uriBuilder) {
        return uriBuilder
                .path("/search/repositories")
                .queryParam("q", buildSearchQuery(searchParams))
                .queryParam("page", page)
                .queryParam("per_page", searchParams.pageSize())
                .build();
    }

    private String buildSearchQuery(GithubSearchParams searchParams) {
        var queryBuilder = new StringBuilder();
        if (searchParams.earliestCreationDate() != null && !searchParams.earliestCreationDate().isEmpty()) {
            queryBuilder.append("created:>=").append(searchParams.earliestCreationDate());
        }
        if (searchParams.language() != null && !searchParams.language().isEmpty()) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" ");
            }
            queryBuilder.append("language:").append(searchParams.language());
        }
        return queryBuilder.toString();
    }
}
