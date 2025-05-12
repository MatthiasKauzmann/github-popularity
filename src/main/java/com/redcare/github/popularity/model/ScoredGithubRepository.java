package com.redcare.github.popularity.model;

public record ScoredGithubRepository(GithubRepository repository, double popularityScore) {
}
