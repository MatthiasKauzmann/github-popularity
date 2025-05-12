package com.redcare.github.popularity.client;

import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.GithubSearchParams;

import java.util.List;

public interface GithubClient {

    /**
     * Retrieves GitHub repositories based on creation date and programming language.
     * The number of results will be limited by GitHub's pagination and rate limits.
     *
     * @param searchParams The search parameters containing criteria such as creation date range,
     *                     programming language and pagination options
     * @return A list of GitHub repositories matching the specified criteria
     */
    List<GithubRepository> getRepositories(GithubSearchParams searchParams);
}
