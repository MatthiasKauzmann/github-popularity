package com.redcare.github.popularity.client;

import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.Language;

import java.util.List;

public interface GithubClient {
    /**
     * Retrieves GitHub repositories based on creation date and programming language.
     * The number of results will be limited by GitHub's pagination and rate limits.
     * *
     * @param earliestCreatedDate The earliest creation date for repositories in ISO format (YYYY-MM-DD), can be null or empty
     * @param language            The programming language to filter repositories by, can be null or empty
     * @return A list of GitHub repositories matching the specified criteria
     */
    List<GithubRepository> getRepositories(String earliestCreatedDate, Language language);
}
