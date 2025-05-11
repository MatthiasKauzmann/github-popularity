package com.redcare.github.popularity.client;

import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.Language;

import java.util.List;

public interface GithubClient {
    /**
     * Gets GitHub repositories based on the provided criteria.
     *
     * @param earliestCreatedDate the earliest created date
     * @param language            the language
     * @return a list of GitHub repositories
     */
    List<GithubRepository> getRepositories(String earliestCreatedDate, Language language);
}
