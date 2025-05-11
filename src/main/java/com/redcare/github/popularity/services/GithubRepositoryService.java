package com.redcare.github.popularity.services;

import com.redcare.github.popularity.client.GithubClient;
import com.redcare.github.popularity.domain.PopularityScorer;
import com.redcare.github.popularity.model.GithubRepositoryDto;
import com.redcare.github.popularity.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService {

    private final GithubClient githubClient;
    private final PopularityScorer popularityScorer;

    /**
     * Retrieves GitHub repositories created after the specified date for a given programming language
     * and calculates their popularity scores.
     * The popularity score is calculated based on the repository's stars count, forks count,
     * and the number of days since the last update.
     *
     * @param earliestCreatedAt The earliest creation date for repositories to include in the results,
     *                          formatted as a string (e.g., "2023-01-01")
     * @param language          The programming language to filter repositories by
     * @return A list of GitHub repository DTOs with calculated popularity scores
     */
    public List<GithubRepositoryDto> getRepositoriesWithPopularityScore(String earliestCreatedAt, Language language) {
        var repositories = githubClient.getRepositories(earliestCreatedAt, language);
        return repositories.stream()
                .map(x -> new GithubRepositoryDto(x, popularityScorer.calculateScore(x.starsCount(), x.forksCount(), getDaysSinceUpdate(x.pushedAt()))))
                .toList();
    }

    private int getDaysSinceUpdate(Instant pushedAt) {
        return (int) ChronoUnit.DAYS.between(pushedAt, Instant.now());
    }
}
