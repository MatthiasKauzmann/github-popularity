package com.redcare.github.popularity.client;

import com.redcare.github.popularity.model.GithubSearchParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GithubClientTest {

    @Autowired
    private GithubClient githubClient;

    static Stream<Arguments> searchParamsTestCases() {
        // Format date as ISO string since GithubSearchParams expects a string
        var yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        var lastWeek = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ISO_DATE);
        return Stream.of(
                // Format: description, date, language, maxPages, pageSize
                Arguments.of(
                        "Single Java repository with page size 1",
                        yesterday,
                        "java",
                        1,
                        1
                ),
                Arguments.of(
                        "Java repositories with page size 2 and 100 results",
                        "2025-01-01",
                        "java",
                        2,
                        100
                ),
                Arguments.of(
                        "No language specified, only date filter",
                        lastWeek,
                        "",
                        1,
                        10
                ),
                Arguments.of(
                        "No date specified, only language filter",
                        "",
                        "python",
                        1,
                        5
                ),
                Arguments.of(
                        "Multiple pages with small page size",
                        lastWeek,
                        "javascript",
                        3,
                        10
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("searchParamsTestCases")
    @DisplayName("Should retrieve GitHub repositories with specified parameters")
    void shouldRetrieveRepositoriesWithCorrectParameters(
            String description,
            String date,
            String language,
            int maxPages,
            int pageSize
    ) {
        // Arrange
        var searchParams = new GithubSearchParams(date, language, maxPages, pageSize);
        // Act
        var result = githubClient.getRepositories(searchParams);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isLessThanOrEqualTo(pageSize * maxPages);
        if (!result.isEmpty()) {
            // Verify language if repositories were found
            var repo = result.get(0);
            assertThat(repo.name()).isNotNull().isNotEmpty();
            // Some repositories might have null language, so only check if it's not null
            if (repo.language() != null && !language.isEmpty()) {
                assertThat(repo.language().toLowerCase()).isEqualTo(language.toLowerCase());
            }
            if (!date.isEmpty()) {
                // Verify the repository was created after the specified date
                var creationDate = Instant.parse(date + "T00:00:00Z");
                assertThat(repo.createdAt()).isAfterOrEqualTo(creationDate);
            }
        }
    }
}
