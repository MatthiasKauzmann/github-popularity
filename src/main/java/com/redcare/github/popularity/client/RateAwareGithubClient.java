package com.redcare.github.popularity.client;

import com.redcare.github.popularity.exception.client.GithubNotModifiedException;
import com.redcare.github.popularity.exception.client.GithubRateLimitException;
import com.redcare.github.popularity.exception.client.GithubUnavailableException;
import com.redcare.github.popularity.exception.client.GithubValidationException;
import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A GitHub client implementation that is aware of GitHub API rate limits.
 * This client adjusts the number of result pages fetched based on whether an access token is provided.
 * With a token, more pages can be fetched due to higher rate limits.
 */
@Service
public class RateAwareGithubClient implements GithubClient {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES_WITH_TOKEN = 30;
    private static final int MAX_PAGES_WITHOUT_TOKEN = 10;

    private final String accessToken;
    private final RestClient restClient;

    public RateAwareGithubClient(@Value("${github.access-token:''}") String accessToken, RestClient restClient) {
        this.accessToken = accessToken;
        this.restClient = restClient;
    }

    /**
     * Retrieves GitHub repositories based on creation date and programming language.
     * The number of results is limited by GitHub's pagination and rate limits.
     * Fetches as many repositories as possible, respecting the number of available result pages
     * and GitHub's API rate limits, without performing any waiting or retrying.
     *
     * @param earliestCreatedDate The earliest creation date for repositories in ISO format (YYYY-MM-DD), can be null or empty
     * @param language            The programming language to filter repositories by, can be null or empty
     * @return A list of GitHub repositories matching the specified criteria
     */
    @Override
    public List<GithubRepository> getRepositories(String earliestCreatedDate, Language language) {
        var maxPages = accessToken != null && !accessToken.isEmpty() ? MAX_PAGES_WITH_TOKEN : MAX_PAGES_WITHOUT_TOKEN;
        var firstResponse = fetchPage(earliestCreatedDate, language, 1);
        List<GithubRepository> result = new ArrayList<>(firstResponse.repositories());
        // calculate how many more pages we need to fetch
        var totalPages = (int) Math.ceil((double) firstResponse.repoCount() / PAGE_SIZE);
        // limit to max allowed pages
        var pagesToFetch = Math.min(totalPages, maxPages);
        // fetch remaining pages (starting from page 2)
        for (int page = 2; page <= pagesToFetch; page++) {
            var response = fetchPage(earliestCreatedDate, language, page);
            result.addAll(response.repositories());
        }
        return result;
    }

    private GithubSearchResponse fetchPage(String earliestCreatedDate, Language language, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", buildSearchQuery(earliestCreatedDate, language))
                        .queryParam("page", page)
                        .queryParam("per_page", PAGE_SIZE)
                        .build())
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

    private String buildSearchQuery(String earliestCreatedDate, Language language) {
        var queryBuilder = new StringBuilder();
        if (earliestCreatedDate != null && !earliestCreatedDate.isEmpty()) {
            queryBuilder.append("created:>=").append(earliestCreatedDate);
        }
        if (language != null) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" ");
            }
            queryBuilder.append("language:").append(language);
        }
        return queryBuilder.toString();
    }
}
