package com.redcare.github.popularity.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redcare.github.popularity.model.GithubRepository;

import java.util.List;

public record GithubSearchResponse(@JsonProperty("total_count") int repoCount,
                                   @JsonProperty("incomplete_results") boolean hasMore, @JsonProperty("items")
                                   List<GithubRepository> repositories) {
}
