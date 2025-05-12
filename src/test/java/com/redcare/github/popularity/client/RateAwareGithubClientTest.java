package com.redcare.github.popularity.client;

import com.redcare.github.popularity.exception.client.*;
import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.GithubSearchParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateAwareGithubClientTest {

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private RestClient.RequestHeadersUriSpec header;
    @Mock
    private GithubSearchResponse response;
    @Mock
    private GithubRepository repository;

    private RateAwareGithubClient client;

    static Stream<Arguments> paginationTestCases() {
        return Stream.of(
                // scenario name, access token, total repos, page size, requested pages, expected fetch calls
                // Max pages scenarios
                Arguments.of("With token and many pages (default page size)", "token123", 5000, 100, 40, 30),
                Arguments.of("Without token and many pages (default page size)", "", 5000, 100, 20, 10),
                // Intermediate page counts
                Arguments.of("With token and 5 pages requested", "token123", 500, 100, 5, 5),
                Arguments.of("Without token and 5 pages requested", "", 500, 100, 5, 5),
                // Different page sizes
                Arguments.of("With token and small page size", "token123", 300, 10, 30, 30),
                Arguments.of("Without token and small page size", "", 300, 10, 10, 10),
                // Edge cases
                Arguments.of("Exactly one page", "", 100, 100, 1, 1),
                Arguments.of("Just over one page", "", 101, 100, 2, 2),
                Arguments.of("Empty result set", "", 0, 100, 1, 1),
                // Boundary cases
                Arguments.of("With token at boundary (30 pages)", "token123", 3000, 100, 30, 30),
                Arguments.of("Without token at boundary (10 pages)", "", 1000, 100, 10, 10),
                // Requested pages less than available
                Arguments.of("Fewer pages requested than available", "token123", 1000, 100, 3, 3),
                // Custom page size with odd total
                Arguments.of("Custom page size with odd total", "", 155, 50, 4, 4)
        );
    }

    static Stream<Arguments> exceptionTestCases() {
        return Stream.of(
                Arguments.of(new GithubNotModifiedException("Not modified"), GithubNotModifiedException.class),
                Arguments.of(new GithubValidationException("Invalid request"), GithubValidationException.class),
                Arguments.of(new GithubUnavailableException("Service unavailable"), GithubUnavailableException.class),
                Arguments.of(new GithubRateLimitException("Rate limit exceeded"), GithubRateLimitException.class)
        );
    }

    @BeforeEach
    void setup() {
        // mock behavior
        when(restClient.get()).thenReturn(header);
        when(header.uri(any(Function.class))).thenReturn(header);
        when(header.retrieve()).thenReturn(responseSpec);
        // arrange
        client = new RateAwareGithubClient("", restClient);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paginationTestCases")
    void shouldFetchRepositoriesRateAware(String scenario, String accessToken, int repoCount,
                                          int pageSize, int requestedPages, int fetchTimesExpected) {
        // arrange
        List<GithubRepository> repositories = new ArrayList<>();
        repositories.add(repository);
        // mock behavior
        when(responseSpec.body(GithubSearchResponse.class)).thenReturn(response);
        when(response.repositories()).thenReturn(repositories);
        when(response.repoCount()).thenReturn(repoCount);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        // create client with appropriate token
        var client = new RateAwareGithubClient(accessToken, restClient);
        // create search params with the specified values
        var searchParams = new GithubSearchParams("2023-01-01", "elixir", requestedPages, pageSize);
        // act
        client.getRepositories(searchParams);
        // assert
        verify(response, times(fetchTimesExpected)).repositories();
    }

    @ParameterizedTest(name = "should throw {1} when API returns appropriate status")
    @MethodSource("exceptionTestCases")
    void shouldThrowAppropriateExceptions(GithubApiException exception, Class<? extends GithubApiException> exceptionClass) {
        // arrange
        when(header.retrieve()).thenThrow(exception);
        var searchParams = new GithubSearchParams("2023-01-01", "elixir", 1, 100);
        // act & assert
        assertThatThrownBy(() -> client.getRepositories(searchParams))
                .isInstanceOf(exceptionClass);
    }
}