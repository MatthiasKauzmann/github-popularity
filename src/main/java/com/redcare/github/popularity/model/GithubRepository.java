package com.redcare.github.popularity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record GithubRepository(long id, @JsonProperty("full_name") String name, @JsonProperty("html_url") String url,
                               @JsonProperty("created_at") Instant createdAt,
                               @JsonProperty("pushed_at") Instant pushedAt,
                               @JsonProperty("stargazers_count") int starsCount,
                               @JsonProperty("forks_count") int forksCount, String language) {
}
