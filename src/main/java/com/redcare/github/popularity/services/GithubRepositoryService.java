package com.redcare.github.popularity.services;

import com.redcare.github.popularity.client.GithubClient;
import com.redcare.github.popularity.domain.PopularityScorer;
import com.redcare.github.popularity.model.GithubSearchParams;
import com.redcare.github.popularity.model.ScoredGithubRepository;
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
     * Retrieves GitHub repositories based on search parameters and calculates a popularity score for each repository.
     * The popularity score is calculated using the repository's star count, fork count, and the time since its last update.
     *
     * @param searchParams The search parameters used to filter GitHub repositories
     * @return A list of GitHub repositories with their calculated popularity scores
     */
    public List<ScoredGithubRepository> getRepositoriesWithPopularityScore(GithubSearchParams searchParams) {
        var repositories = githubClient.getRepositories(searchParams);
        return repositories.stream()
                .map(x -> new ScoredGithubRepository(x, popularityScorer.calculateScore(x.starsCount(), x.forksCount(), getDaysSinceUpdate(x.pushedAt()))))
                .toList();
    }

    private int getDaysSinceUpdate(Instant pushedAt) {
        return (int) ChronoUnit.DAYS.between(pushedAt, Instant.now());
    }
}
