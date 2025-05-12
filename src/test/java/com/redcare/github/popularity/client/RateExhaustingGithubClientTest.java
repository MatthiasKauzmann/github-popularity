package com.redcare.github.popularity.client;

import com.redcare.github.popularity.exception.client.GithubNotModifiedException;
import com.redcare.github.popularity.exception.client.GithubRateLimitException;
import com.redcare.github.popularity.exception.client.GithubUnavailableException;
import com.redcare.github.popularity.exception.client.GithubValidationException;
import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
class RateExhaustingGithubClientTest {

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

    private RateExhaustingGithubClient client;

    static Stream<TestCase> testCases() {
        return Stream.of(
                new TestCase("withToken", "token123", 5000, 30, 30),
                new TestCase("withoutToken", "", 5000, 10, 10),
                new TestCase("onlyOnePage", "", 50, 1, 1),
                new TestCase("emptyResult", "", 0, 1, 0)
        );
    }

    @BeforeEach
    void setup() {
        // mock behavior
        when(restClient.get()).thenReturn(header);
        when(header.uri(any(Function.class))).thenReturn(header);
        when(header.retrieve()).thenReturn(responseSpec);
        // arrange
        client = new RateExhaustingGithubClient("", restClient);
    }

    @MethodSource("testCases")
    @ParameterizedTest(name = "{0}")
    void shouldFetchRepositoriesRateAware(TestCase testCase) {
        List<GithubRepository> repositories = new ArrayList<>();
        repositories.add(repository);
        // mock behavior
        when(responseSpec.body(GithubSearchResponse.class)).thenReturn(response);
        when(response.repositories()).thenReturn(repositories);
        when(response.repoCount()).thenReturn(testCase.repoCount());
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        // arrange
        var client = new RateExhaustingGithubClient(testCase.accessToken(), restClient);
        // act
        client.getRepositories("", Language.ELIXIR);
        verify(response, times(testCase.fetchTimesExpected())).repositories();
    }

    @Test
    void shouldThrowGithubNotModifiedExceptionOn304() {
        when(header.retrieve()).thenThrow(new GithubNotModifiedException(""));
        assertThatThrownBy(() -> client.getRepositories("", Language.ELIXIR))
                .isInstanceOf(GithubNotModifiedException.class);
    }

    @Test
    void shouldThrowGithubValidationExceptionOn422() {
        when(header.retrieve()).thenThrow(new GithubValidationException(""));
        assertThatThrownBy(() -> client.getRepositories("", Language.ELIXIR))
                .isInstanceOf(GithubValidationException.class);
    }

    @Test
    void shouldThrowGithubUnavailableExceptionOn503() {
        when(header.retrieve()).thenThrow(new GithubUnavailableException(""));
        assertThatThrownBy(() -> client.getRepositories("", Language.ELIXIR))
                .isInstanceOf(GithubUnavailableException.class);
    }

    @Test
    void shouldThrowGithubRateLimitExceptionOn403() {
        when(header.retrieve()).thenThrow(new GithubRateLimitException(""));
        assertThatThrownBy(() -> client.getRepositories("", Language.ELIXIR))
                .isInstanceOf(GithubRateLimitException.class);
    }

}